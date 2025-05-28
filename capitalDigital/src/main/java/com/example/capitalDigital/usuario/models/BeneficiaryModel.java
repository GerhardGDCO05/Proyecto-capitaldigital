package com.example.capitalDigital.usuario.models;

public class BeneficiaryModel {
    private String beneficiaryName;
    private String id;
    private String accountNumber;
    private String bank;

    // Constructores
    public BeneficiaryModel() {}

    public BeneficiaryModel(String beneficiaryName, String id, String accountNumber, String bank) {
        this.beneficiaryName = beneficiaryName;
        this.id = id;
        this.accountNumber = accountNumber;
        this.bank = bank;
    }

    // Getters y setters
    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "Beneficiary{" +
                "beneficiaryName='" + beneficiaryName + '\'' +
                ", ID='" + id + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bank='" + bank + '\'' +
                '}';
    }
}