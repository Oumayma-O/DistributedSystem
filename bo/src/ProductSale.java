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

    public ProductSale(Date date, String region, String product, int qty, float cost, double amt, float tax, double total, boolean sent) {

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

    public ProductSale() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setAmt(double amt) {
        this.amt = amt ;
    }

    public void setTax(float tax) {
        this.tax = tax ;

    }

    public void setTotal(double total) {
        this.total=total;
    }

    public void setSent(boolean sent) {
        this.sent= sent;
    }

    public Object getAmt() {
        return this.amt;
    }

    public Object getTax() {
        return this.tax;
    }

    public Object getTotal() {
        return  this.total;
    }

    public Object isSent() {
        return  this.sent ;
    }
}