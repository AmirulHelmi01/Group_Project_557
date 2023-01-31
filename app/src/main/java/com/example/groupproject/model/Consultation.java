package com.example.groupproject.model;

public class Consultation {
    private int consultation_id; // Primary key
    private int student_id; //Foreign key student
    private int lecturer_id; //Foreign key lecturer
    private User student; //student object
    private User lecturer; //lecturer object
    private String consult_date;
    private String consult_time;
    private String purpose;
    private String status;

    public Consultation()
    {

    }

    public Consultation(int id, int student_id, int lecturer_id, String consult_date, String consult_time, String purpose, String status) {
        this.consultation_id = id;
        this.student_id = student_id;
        this.lecturer_id = lecturer_id;
        this.consult_date = consult_date;
        this.consult_time = consult_time;
        this.student = student;
        this.lecturer = lecturer;
        this.purpose = purpose;
        this.status = status;
    }

    public void updateConsult(){

    }

    public User getStudent() { return student; }

    public void setStudent(User student) { this.student = student; }

    public User getLecturer() { return lecturer; }

    public void setLecturer(User lecturer) { this.lecturer = lecturer; }

    public int getConsultation_id() {
        return consultation_id;
    }

    public void setConsultation_id(int id) {
        this.consultation_id = id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getLecturer_id() {
        return lecturer_id;
    }

    public void setLecturer_id(int lecturer_id) {
        this.lecturer_id = lecturer_id;
    }

    public String getConsult_date() { return consult_date; }

    public void setConsult_date(String consult_date) { this.consult_date = consult_date; }

    public String getConsult_time() { return consult_time; }

    public void setConsult_time(String consult_time) { this.consult_time = consult_time; }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + consultation_id +
                ", student_id=" + student_id +
                ", lecturer_id=" + lecturer_id +
                ", student=" + student +
                ", lecturer=" + lecturer +
                ", consult_date='" + consult_date + '\'' +
                ", consult_time='" + consult_time + '\'' +
                ", purpose='" + purpose + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}