package model;

import java.time.LocalDate;

/**
 * Author: Alvin Chiu
 * Created: 8/1/2026
 * Current version: V1.0 - 8/1/2026
 * Description: This class will be used to store the grades of a student in a course.
 *  It will have the following attributes:
 *  - courseId: The ID of the course.
 *  - studentId: The ID of the student.
 *  - score: The grade of the student in the course.
 *  - entryDate: The date the grade was recorded.
 */

public class Grade {
    public static final int MAX_GRADE = 100;
    public static final int MIN_GRADE = 0;

    private double score;
    private LocalDate entryDate;
    private String studentName;

    private final String assignmentId;
    private final String courseId;
    private final String studentId;

    public Grade(String courseId, String studentId,String assignmentId, double score){
        this.courseId = courseId;
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.score = score;
        this.entryDate = LocalDate.now(); // Set to current date
    }

    public Grade(String courseId, String studentId,String assignmentId, int score){
        this.courseId = courseId;
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.score = score;
        this.entryDate = LocalDate.now(); // Set to current date
    }

    public void setScore(double score){
        if (score >= MIN_GRADE && score <= MAX_GRADE) {
            this.score = score;
        }else{
            //TODO: display error message as a popup.
            System.out.println("Score must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
    }

    public double getScore(){
        return this.score;
    }

    public void setDate(LocalDate date){
        this.entryDate = date;
    }

    public LocalDate getDate(){
        return this.entryDate;
    }

    public String getCourseId(){
        return this.courseId;
    }

    public String getStudentId(){
        return this.studentId;
    }

    public String getAssignmentId(){
        return this.assignmentId;
    }
    public String getStudentName() {
        return this.studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

}
