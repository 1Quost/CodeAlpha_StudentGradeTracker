package miniproject2_final.studentgradetracker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class StudentGradeTracker extends Application {

    private ArrayList<Student> students = new ArrayList<>();
    private TextField nameField = new TextField();
    private TextField gradeField = new TextField();
    private ComboBox<String> majorField = new ComboBox<>();
    private TextArea resultArea = new TextArea();
    private PieChart gradeChart;
    private LineChart<Number, Number> progressChart;
    private Label statsLabel = new Label();
    private VBox studentListBox = new VBox(8);
    private ScrollPane studentScrollPane;

    // Theme colors
    private static final String PRIMARY_COLOR = "#667eea";
    private static final String SECONDARY_COLOR = "#764ba2";
    private static final String ACCENT_COLOR = "#f093fb";
    private static final String SUCCESS_COLOR = "#4facfe";
    private static final String WARNING_COLOR = "#f093fb";
    private static final String DANGER_COLOR = "#fa709a";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎓 Student Grade Tracker");

        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle(createGradientBackground());

        // Create header
        VBox header = createHeader();
        root.setTop(header);

        // Create main content
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));

        // Left panel - Input form
        VBox leftPanel = createInputPanel();

        // Center panel - Statistics and charts
        VBox centerPanel = createStatsPanel();

        // Right panel - Student list
        VBox rightPanel = createStudentListPanel();

        mainContent.getChildren().addAll(leftPanel, centerPanel, rightPanel);
        root.setCenter(mainContent);


        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(createCustomCSS());

        primaryStage.setScene(scene);
        primaryStage.show();

        // Add entrance animation
        addEntranceAnimation(root);
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #cbcdff");

        Label titleLabel = new Label("🎓 STUDENT GRADE TRACKER");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.BLUE);
        titleLabel.setEffect(new DropShadow(10, Color.HOTPINK));

        Label subtitleLabel = new Label("Advanced Academic Performance Management System");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.NAVY);

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private VBox createInputPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(300);
        panel.setStyle(createPanelStyle());
        panel.setPadding(new Insets(20));

        Label panelTitle = new Label("📝 Add New Student");
        panelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        panelTitle.setTextFill(Color.BLACK);

        // Enhanced input fields
        nameField.setPromptText("Enter student name...");
        nameField.setStyle(createInputFieldStyle());
        nameField.setPrefHeight(40);

        gradeField.setPromptText("Enter grade (0-100)...");
        gradeField.setStyle(createInputFieldStyle());
        gradeField.setPrefHeight(40);

        // Major selection
        majorField.setPromptText("Select major...");
        majorField.setItems(FXCollections.observableArrayList(
                "Computer Science", "Engineering", "Business Administration", "Medicine",
                "Law", "Psychology", "Biology", "Mathematics", "Physics", "Chemistry",
                "Economics", "Political Science", "Literature", "History", "Philosophy",
                "Architecture", "Fine Arts", "Music", "Education", "Nursing"
        ));
        majorField.setStyle(createInputFieldStyle());
        majorField.setPrefHeight(40);

        // Enhanced buttons
        Button addButton = createStyledButton("➕ Add Student", SUCCESS_COLOR);
        Button clearButton = createStyledButton("🗑️ Clear Form", WARNING_COLOR);

        addButton.setOnAction(e -> addStudentWithAnimation());
        clearButton.setOnAction(e -> clearForm());

        HBox buttonBox = new HBox(10, addButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(
                panelTitle,
                new Label("Student Name:") {{ setTextFill(Color.DARKBLUE); setFont(Font.font("Arial", FontWeight.BOLD, 12)); }},
                nameField,
                new Label("Grade:") {{ setTextFill(Color.DARKBLUE); setFont(Font.font("Arial", FontWeight.BOLD, 12)); }},
                gradeField,
                new Label("Major:") {{ setTextFill(Color.DARKBLUE); setFont(Font.font("Arial", FontWeight.BOLD, 12)); }},
                majorField,
                buttonBox
        );

        return panel;
    }

    private VBox createStatsPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(400);
        panel.setStyle(createPanelStyle());
        panel.setPadding(new Insets(20));

        Label panelTitle = new Label("📊 Statistics & Analytics");
        panelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        panelTitle.setTextFill(Color.BLACK);

        // Stats display
        statsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        statsLabel.setTextFill(Color.DARKBLUE);
        statsLabel.setWrapText(true);

        // Grade distribution chart
        gradeChart = new PieChart();
        gradeChart.setTitle("Grade Distribution");
        gradeChart.setPrefSize(300, 250);
        gradeChart.setStyle("-fx-background-color: transparent;");

        // Progress chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Student #");
        yAxis.setLabel("Grade");

        progressChart = new LineChart<>(xAxis, yAxis);
        progressChart.setTitle("Grade Progression");
        progressChart.setPrefSize(300, 200);
        progressChart.setStyle("-fx-background-color: transparent;");

        Platform.runLater(() -> {
            if (gradeChart.lookup(".chart-title") != null) {
                gradeChart.lookup(".chart-title").setStyle("-fx-text-fill: navy;");
            }
            if (progressChart.lookup(".chart-title") != null) {
                progressChart.lookup(".chart-title").setStyle("-fx-text-fill: navy;");
            }
        });

        Button reportButton = createStyledButton("📋 Generate Report", PRIMARY_COLOR);
        reportButton.setOnAction(e -> showResults());

        panel.getChildren().addAll(
                panelTitle,
                statsLabel,
                gradeChart,
                progressChart,
                reportButton
        );

        return panel;
    }

    private void showResults(){
        Stage reportStage = new Stage();
        reportStage.setTitle("Detailed Student Report");

        VBox reportLayout = new VBox(15);
        reportLayout.setPadding(new Insets(20));
        reportLayout.setStyle(createPanelStyle());

        Label reportTitle = new Label("📄 Detailed Student Report");
        reportTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        reportTitle.setTextFill(Color.DARKBLUE);

        TextArea reportContent = new TextArea();
        reportContent.setEditable(false);
        reportContent.setWrapText(true);
        reportContent.setStyle(createTextAreaStyle());
        reportContent.setPrefHeight(600);
        reportContent.setPrefWidth(500);

        StringBuilder reportText = new StringBuilder();
        generateReportContent(reportText);
        reportContent.setText(reportText.toString());

        Button closeButton = createStyledButton("Close", DANGER_COLOR);
        closeButton.setOnAction(e -> reportStage.close());

        reportLayout.getChildren().addAll(reportTitle, reportContent, closeButton);
        reportLayout.setAlignment(Pos.CENTER);

        Scene reportScene = new Scene(reportLayout);
        reportStage.setScene(reportScene);
        reportStage.show();
    }

    private void generateReportContent(StringBuilder sb) {
        if (students.isEmpty()) {
            sb.append("📄 No student data available for report generation.");
            return;
        }

        sb.append("🎓 COMPREHENSIVE STUDENT GRADE REPORT BY MAJOR\n");
        sb.append("=".repeat(55)).append("\n\n");

        // Group by major
        Map<String, List<Student>> byMajor = students.stream()
                .collect(Collectors.groupingBy(Student::getMajor));

        byMajor.forEach((major, studentList) -> {
            sb.append(String.format("🏛️ %s (%d students)\n", major, studentList.size()));
            sb.append("-".repeat(40)).append("\n");

            studentList.forEach(s ->
                    sb.append(String.format("  • %s: %.1f %s\n",
                            s.getName(), s.getGrade(), getGradeEmoji(s.getGrade())))
            );

            double majorAvg = studentList.stream().mapToDouble(Student::getGrade).average().orElse(0);
            sb.append(String.format("  📊 Major Average: %.2f\n\n", majorAvg));
        });

        // Overall statistics
        double overallAvg = students.stream().mapToDouble(Student::getGrade).average().orElse(0);
        long excellentCount = students.stream().mapToLong(s -> s.getGrade() >= 90 ? 1 : 0).sum();
        long goodCount = students.stream().mapToLong(s -> s.getGrade() >= 80 && s.getGrade() < 90 ? 1 : 0).sum();
        long averageCount = students.stream().mapToLong(s -> s.getGrade() >= 70 && s.getGrade() < 80 ? 1 : 0).sum();
        long needsImprovementCount = students.stream().mapToLong(s -> s.getGrade() < 70 ? 1 : 0).sum();

        sb.append("🏆 PERFORMANCE SUMMARY\n");
        sb.append("=".repeat(30)).append("\n");
        sb.append(String.format("📊 Overall Class Average: %.2f\n", overallAvg));
        sb.append(String.format("🌟 Excellent (90-100): %d students\n", excellentCount));
        sb.append(String.format("✅ Good (80-89): %d students\n", goodCount));
        sb.append(String.format("📈 Average (70-79): %d students\n", averageCount));
        sb.append(String.format("📚 Needs Improvement (<70): %d students\n", needsImprovementCount));

        sb.append(String.format("\n📅 Report Generated: %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }


    private VBox createStudentListPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(350);
        panel.setStyle(createPanelStyle());
        panel.setPadding(new Insets(20));

        Label panelTitle = new Label("👥 Student List");
        panelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        panelTitle.setTextFill(Color.BLACK);

        studentListBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");
        studentListBox.setPadding(new Insets(10));

        studentScrollPane = new ScrollPane(studentListBox);
        studentScrollPane.setFitToWidth(true);
        studentScrollPane.setPrefHeight(400);
        studentScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Search students...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            studentListBox.getChildren().clear();
            students.stream()
                    .filter(s -> s.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                            s.getMajor().toLowerCase().contains(newVal.toLowerCase()))
                    .forEach(this::addStudentToVisualList);
        });


        Button sortButton = createStyledButton("🔄 Sort by Grade", ACCENT_COLOR);
        Button deleteAllButton = createStyledButton("🗑️ Clear All", DANGER_COLOR);

        sortButton.setOnAction(e -> sortStudentsByGrade());
        deleteAllButton.setOnAction(e -> clearAllStudents());

        controlBox.getChildren().addAll(sortButton, deleteAllButton);

        panel.getChildren().addAll(panelTitle, searchField ,studentScrollPane, controlBox);
        return panel;
    }


    private void addStudentWithAnimation() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();
        String major = majorField.getValue();

        if (name.isEmpty() || gradeText.isEmpty() || major == null) {
            showEnhancedAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {
                showEnhancedAlert("Error", "Grade must be between 0 and 100.", Alert.AlertType.ERROR);
                return;
            }

            Student newStudent = new Student(name, grade, major);
            students.add(newStudent);

            // Add to visual list with animation
            addStudentToVisualList(newStudent);

            clearForm();
            updateCharts();
            updateStats();

            showEnhancedAlert("Success", "Student added successfully! 🎉", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showEnhancedAlert("Error", "Please enter a valid number for grade.", Alert.AlertType.ERROR);
        }
    }

    private void addStudentToVisualList(Student student) {
        HBox studentBox = new HBox(10);
        studentBox.setAlignment(Pos.CENTER_LEFT);
        studentBox.setPadding(new Insets(10));
        studentBox.setStyle(createStudentCardStyle(student.getGrade()));

        Label nameLabel = new Label(student.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.DARKBLUE);

        Label gradeLabel = new Label(String.format("%.1f", student.getGrade()));
        gradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gradeLabel.setTextFill(getGradeColor(student.getGrade()));

        Label majorLabel = new Label(student.getMajor());
        majorLabel.setFont(Font.font("Arial", 12));
        majorLabel.setTextFill(Color.DARKBLUE);

        Button deleteButton = new Button("❌");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff6b6b; -fx-font-size: 12;");
        deleteButton.setOnAction(e -> removeStudent(student, studentBox));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox infoBox = new VBox(2, nameLabel, majorLabel);
        studentBox.getChildren().addAll(infoBox, spacer, gradeLabel, deleteButton);

        // Add with fade-in animation
        studentBox.setOpacity(0);
        studentListBox.getChildren().add(studentBox);

        FadeTransition fade = new FadeTransition(Duration.millis(500), studentBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void removeStudent(Student student, HBox studentBox) {
        students.remove(student);

        FadeTransition fade = new FadeTransition(Duration.millis(300), studentBox);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            studentListBox.getChildren().remove(studentBox);
            updateCharts();
            updateStats();
        });
        fade.play();
    }

    private void updateCharts() {
        // Update pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        Map<String, Long> gradeRanges = students.stream()
                .collect(Collectors.groupingBy(this::getGradeRange, Collectors.counting()));

        gradeRanges.forEach((range, count) ->
                pieChartData.add(new PieChart.Data(range, count))
        );

        gradeChart.setData(pieChartData);

        // Update line chart
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Student Grades");

        for (int i = 0; i < students.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, students.get(i).getGrade()));
        }

        progressChart.getData().clear();
        progressChart.getData().add(series);
    }

    private void updateStats() {
        if (students.isEmpty()) {
            statsLabel.setText("📊 No data available");
            return;
        }

        double average = students.stream().mapToDouble(Student::getGrade).average().orElse(0);
        double highest = students.stream().mapToDouble(Student::getGrade).max().orElse(0);
        double lowest = students.stream().mapToDouble(Student::getGrade).min().orElse(0);

        String stats = String.format(
                "📈 Total Students: %d\n" +
                        "📊 Class Average: %.2f\n" +
                        "🏆 Highest Grade: %.1f\n" +
                        "📉 Lowest Grade: %.1f\n" +
                        "🎯 Pass Rate: %.1f%%",
                students.size(),
                average,
                highest,
                lowest,
                students.stream().mapToDouble(Student::getGrade).filter(g -> g >= 60).count() * 100.0 / students.size()
        );

        statsLabel.setText(stats);
    }

    private void showEnhancedReport() {
        if (students.isEmpty()) {
            resultArea.setText("📄 No student data available for report generation.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🎓 COMPREHENSIVE STUDENT GRADE REPORT BY MAJOR\n");
        sb.append("=" .repeat(55)).append("\n\n");

        // Group by major
        Map<String, List<Student>> byMajor = students.stream()
                .collect(Collectors.groupingBy(Student::getMajor));

        byMajor.forEach((major, studentList) -> {
            sb.append(String.format("🏛️ %s (%d students)\n", major, studentList.size()));
            sb.append("-".repeat(40)).append("\n");

            studentList.forEach(s ->
                    sb.append(String.format("  • %s: %.1f %s\n",
                            s.getName(), s.getGrade(), getGradeEmoji(s.getGrade())))
            );

            double majorAvg = studentList.stream().mapToDouble(Student::getGrade).average().orElse(0);
            sb.append(String.format("  📊 Major Average: %.2f\n\n", majorAvg));
        });

        // Overall statistics
        double overallAvg = students.stream().mapToDouble(Student::getGrade).average().orElse(0);
        long excellentCount = students.stream().mapToLong(s -> s.getGrade() >= 90 ? 1 : 0).sum();
        long goodCount = students.stream().mapToLong(s -> s.getGrade() >= 80 && s.getGrade() < 90 ? 1 : 0).sum();
        long averageCount = students.stream().mapToLong(s -> s.getGrade() >= 70 && s.getGrade() < 80 ? 1 : 0).sum();
        long needsImprovementCount = students.stream().mapToLong(s -> s.getGrade() < 70 ? 1 : 0).sum();

        sb.append("🏆 PERFORMANCE SUMMARY\n");
        sb.append("=" .repeat(30)).append("\n");
        sb.append(String.format("📊 Overall Class Average: %.2f\n", overallAvg));
        sb.append(String.format("🌟 Excellent (90-100): %d students\n", excellentCount));
        sb.append(String.format("✅ Good (80-89): %d students\n", goodCount));
        sb.append(String.format("📈 Average (70-79): %d students\n", averageCount));
        sb.append(String.format("📚 Needs Improvement (<70): %d students\n", needsImprovementCount));

        sb.append(String.format("\n📅 Report Generated: %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        resultArea.setText(sb.toString());
    }

    private void sortStudentsByGrade() {
        students.sort((s1, s2) -> Double.compare(s2.getGrade(), s1.getGrade()));
        refreshStudentList();
    }

    private void clearAllStudents() {
        if (students.isEmpty()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear All");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will remove all students from the tracker.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                students.clear();
                refreshStudentList();
                updateCharts();
                updateStats();
                resultArea.clear();
            }
        });
    }

    private void refreshStudentList() {
        studentListBox.getChildren().clear();
        students.forEach(this::addStudentToVisualList);
    }

    private void clearForm() {
        nameField.clear();
        gradeField.clear();
        majorField.setValue(null);
    }

    private void addEntranceAnimation(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);

        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.play();
    }

    // Helper methods for styling and utilities
    private String createGradientBackground() {
        return String.format(
                "-fx-background-color:#cbcdff;",
                PRIMARY_COLOR, SECONDARY_COLOR
        );
    }

    private String createPanelStyle() {
        return "-fx-background-color: #cbdfff; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);";
    }

    private String createInputFieldStyle() {
        return "-fx-background-color: rgba(255,255,255,0.9); " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 14; " +
                "-fx-padding: 10;";
    }

    private String createTextAreaStyle() {
        return "-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-background-radius: 10; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 12;";
    }


    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);",
                color
        ));

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-scale-x: 1.05; -fx-scale-y: 1.05;", "")));

        return button;
    }

    private String createStudentCardStyle(double grade) {
        String baseStyle = "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); ";

        if (grade >= 90) return baseStyle + "-fx-background-color: linear-gradient(90deg, #e8f5e8 0%, #d4edda 100%);";
        else if (grade >= 80) return baseStyle + "-fx-background-color: linear-gradient(90deg, #e3f2fd 0%, #bbdefb 100%);";
        else if (grade >= 70) return baseStyle + "-fx-background-color: linear-gradient(90deg, #fff3e0 0%, #ffcc80 100%);";
        else return baseStyle + "-fx-background-color: linear-gradient(90deg, #ffebee 0%, #ffcdd2 100%);";
    }

    private String getGradeRange(Student student) {
        double grade = student.getGrade();
        if (grade >= 90) return "Excellent (90-100)";
        else if (grade >= 80) return "Good (80-89)";
        else if (grade >= 70) return "Average (70-79)";
        else return "Needs Improvement (<70)";
    }

    private Color getGradeColor(double grade) {
        if (grade >= 90) return Color.DARKGREEN;
        else if (grade >= 80) return Color.DARKBLUE;
        else if (grade >= 70) return Color.DARKORANGE;
        else return Color.DARKRED;
    }

    private String getGradeEmoji(double grade) {
        if (grade >= 95) return "🏆";
        else if (grade >= 90) return "🌟";
        else if (grade >= 80) return "✅";
        else if (grade >= 70) return "📈";
        else return "📚";
    }

    private String createCustomCSS() {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(
                (".chart-title { -fx-text-fill: navy; -fx-font-weight: bold; }" +
                        ".chart-legend { -fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 5; }" +
                        ".scroll-pane { -fx-background: transparent; }" +
                        ".scroll-pane .viewport { -fx-background-color: transparent; }" +
                        ".scroll-pane .scroll-bar { -fx-background-color: rgba(255,255,255,0.3); }")
                        .getBytes()
        );
    }

    private void showEnhancedAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(45deg, #667eea 0%, #764ba2 100%);");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: black; -fx-font-size: 14;");

        alert.showAndWait();
    }

    // Student class
    private static class Student {
        private final String name;
        private final double grade;
        private final String major;
        private final LocalDateTime timestamp;

        public Student(String name, double grade, String major) {
            this.name = name;
            this.grade = grade;
            this.major = major;
            this.timestamp = LocalDateTime.now();
        }

        public String getName() { return name; }
        public double getGrade() { return grade; }
        public String getMajor() { return major; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
