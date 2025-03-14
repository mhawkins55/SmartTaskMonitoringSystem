import java.util.Date;

public class Task {
    private int taskId;
    private String title;
    private String description;
    private int assignedTo;
    private int createdBy;
    private String priority;
    private String status;
    private java.sql.Date dueDate;
    private String assignedBy;

    public Task(int taskId, String title, String description, int assignedTo, int createdBy, String priority, String status, java.sql.Date dueDate, String assignedBy) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
        this.assignedBy = assignedBy;
    }

    public int getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getAssignedTo() { return assignedTo; }
    public int getCreatedBy() { return createdBy; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public java.sql.Date getDueDate() { return dueDate; }
    public String getAssignedBy() { return assignedBy; }
}
