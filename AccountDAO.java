import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object - all SQL for the accounts table lives here.
 * Keeping this separate from Main is the standard DAO pattern interviewers look for.
 */
public class AccountDAO {

    // CREATE
    public int createAccount(Account acc) throws SQLException {
        String sql = "INSERT INTO accounts (holder_name, account_type, balance) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, acc.getHolderName());
            ps.setString(2, acc.getAccountType());
            ps.setBigDecimal(3, acc.getBalance());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    // READ single
    public Account getAccountByNo(int accountNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        }
    }

    // READ all
    public List<Account> getAllAccounts() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_no";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // UPDATE holder name / type
    public boolean updateAccount(Account acc) throws SQLException {
        String sql = "UPDATE accounts SET holder_name = ?, account_type = ? WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, acc.getHolderName());
            ps.setString(2, acc.getAccountType());
            ps.setInt(3, acc.getAccountNo());

            return ps.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deleteAccount(int accountNo) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountNo);
            return ps.executeUpdate() > 0;
        }
    }

    // DEPOSIT
    public boolean deposit(int accountNo, BigDecimal amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, accountNo);
            return ps.executeUpdate() > 0;
        }
    }

    // WITHDRAW (checks balance first)
    public boolean withdraw(int accountNo, BigDecimal amount) throws SQLException {
        Account acc = getAccountByNo(accountNo);
        if (acc == null) throw new SQLException("Account not found: " + accountNo);
        if (acc.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance. Available: Rs. " + acc.getBalance());
        }

        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, accountNo);
            return ps.executeUpdate() > 0;
        }
    }

    // TRANSFER - demonstrates JDBC transaction handling (commit/rollback)
    public void transfer(int fromAccount, int toAccount, BigDecimal amount) throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // start transaction

            // check sender balance
            String checkSql = "SELECT balance FROM accounts WHERE account_no = ? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, fromAccount);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) throw new SQLException("Sender account not found");
                BigDecimal balance = rs.getBigDecimal("balance");
                if (balance.compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient balance for transfer");
                }
            }

            // debit sender
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE account_no = ?")) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, fromAccount);
                ps.executeUpdate();
            }

            // credit receiver
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance = balance + ? WHERE account_no = ?")) {
                ps.setBigDecimal(1, amount);
                ps.setInt(2, toAccount);
                int rows = ps.executeUpdate();
                if (rows == 0) throw new SQLException("Receiver account not found");
            }

            con.commit();
        } catch (SQLException | IllegalStateException e) {
            if (con != null) con.rollback();
            throw e instanceof SQLException ? (SQLException) e : new SQLException(e.getMessage());
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("account_no"),
                rs.getString("holder_name"),
                rs.getString("account_type"),
                rs.getBigDecimal("balance")
        );
    }
}