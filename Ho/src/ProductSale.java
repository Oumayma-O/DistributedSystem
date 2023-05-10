import java.sql.Date;

class ProductSale {

    private int id;
    private Date date;
    private String region;
    private String product;
    private int qty;
    private float cost;
    private double amt;
    private float tax;
    private double total;
    private boolean sent;

    public ProductSale( Date date, String region, String product, int qty, float cost, double amt, float tax, double total, boolean sent) {

        this.date = date;
        this.region = region;
        this.product = product;
        this.qty = qty;
        this.cost = cost;
        this.amt = amt;
        this.tax = tax;
        this.total = total;
        this.sent = sent;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getRegion() {
        return region;
    }

    public String getProduct() {
        return product;
    }

    public int getQty() {
        return qty;
    }

    public float getCost() {
        return cost;
    }

    public double getAmt() {
        return amt;
    }

    public float getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }

    public boolean isSent() {
        return sent;
    }

}
