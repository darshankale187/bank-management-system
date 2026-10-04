
import java.math.BigDecimal;

/**
 * POJO representing a row in the accounts table.
 */
public class Account {

    private int accountNo;
    private String holderName;
    private String accountType;
    private BigDecimal balance;

    public Account() {
    }

    public Account(int accountNo, String holderName, String accountType, BigDecimal balance) {
        this.accountNo = accountNo;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Constructor for creating a new account (no account number yet)
    public Account(String holderName, String accountType, BigDecimal balance) {
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = balance;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("%-10d %-20s %-10s Rs. %s",
                accountNo, holderName, accountType, balance.toString());
    }
}
