public class InvoiceItem {
    private int invoice_id;
    private int item_id;
    private int quantity;

    public InvoiceItem(int invoice_id, int item_id, int quantity) {
        this.invoice_id = invoice_id;
        this.item_id = item_id;
        this.quantity = quantity;
    }

    public int getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(int invoice_id) {
        this.invoice_id = invoice_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}