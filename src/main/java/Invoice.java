import java.sql.Date;

public class Invoice {
    private final int invoice_id;
    private int customer_id;

    private Date date;

    private Date due_date;

    private double total;

    public Invoice(int invoice_id, int customer_id, Date date, Date due_date, double total) {
        this.invoice_id = invoice_id;
        this.customer_id = customer_id;
        this.date = date;
        this.due_date = due_date;
        this.total = total;
    }

    public int getInvoice_id() {
        return invoice_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}