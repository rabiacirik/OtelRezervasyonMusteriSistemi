package model;

// Builder Deseni kullanılarak oluşturulacak Müşteri Veri Nesnesi
public class CustomerDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String tcNo;

    // Constructor private yapılır, sadece Builder erişebilir
    private CustomerDTO(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.phone = builder.phone;
        this.email = builder.email;
        this.tcNo = builder.tcNo;
    }

    // Getter metotları...
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getTcNo() { return tcNo; }

    // --- STATIC BUILDER SINIFI ---
    public static class Builder {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        private String email;
        private String tcNo;

        // Zorunlu alanlar constructor'da alınabilir
        public Builder(String username, String password, String tcNo) {
            this.username = username;
            this.password = password;
            this.tcNo = tcNo;
        }

        // Opsiyonel alanlar için zincirleme metotlar (Fluent Interface)
        public Builder firstName(String val) { firstName = val; return this; }
        public Builder lastName(String val) { lastName = val; return this; }
        public Builder phone(String val) { phone = val; return this; }
        public Builder email(String val) { email = val; return this; }

        // Son aşama: Nesneyi inşa et
        public CustomerDTO build() {
            return new CustomerDTO(this);
        }
    }
}