package aaacomms.aaa_app;

public class JobsListDataModel {

    int jobNo;
    String customer;

    JobsListDataModel(int jobNo, String customer) {
        this.jobNo = jobNo;
        this.customer = customer;
    }

    public int getJobNo() {
        return jobNo;
    }

    public String getCustomer() {
        return customer;
    }
}