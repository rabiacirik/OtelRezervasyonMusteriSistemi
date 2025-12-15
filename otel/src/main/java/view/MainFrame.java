package view;

import dao.HotelDAO;
import model.CustomerDTO;
import patterns.factory.RoomFactory;
import patterns.observer.NotificationService;
import patterns.observer.Observer;
import patterns.strategy.DiscountPricingStrategy;
import patterns.strategy.NormalPricingStrategy;
import patterns.strategy.PricingStrategy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * Ana Uygulama Arayüzü (MainFrame)
 * - Observer Tasarım Deseni: 'implements Observer' ile bildirimleri dinler.
 * - Facade/DAO Kullanımı: Veritabanı işlemleri için 'dao' nesnesini kullanır.
 * - Strategy ve Builder desenlerini kullanıcı arayüzü üzerinden tetikler.
 */
public class MainFrame extends JFrame implements Observer {
    private HotelDAO dao = new HotelDAO();
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private NotificationService notificationService = new NotificationService();
    private int currentUserId = 0;
    private String returnToPanel = "LOGIN";

    public MainFrame() {
        // Observer Deseni: Kendimizi bildirim servisine abone yapıyoruz.
        notificationService.subscribe(this);
        setTitle("Otel Otomasyonu (PRJ-4) - Design Patterns Edition");
        setSize(1150, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createCustomerPanel(), "CUSTOMER");
        mainPanel.add(createStaffPanel(), "STAFF");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // --- 1. LOGIN ---
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10);
        JTextField txtUser = new JTextField(15); JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Giriş Yap"); JButton btnRegister = new JButton("Kayıt Ol");
        panel.add(new JLabel("Kullanıcı / Email / TC:"), gbc); gbc.gridx = 1; panel.add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Şifre:"), gbc); gbc.gridx = 1; panel.add(txtPass, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(btnLogin, gbc); gbc.gridx = 1; gbc.gridy = 3; panel.add(btnRegister, gbc);
        btnLogin.addActionListener(e -> {
            String r = dao.login(txtUser.getText(), new String(txtPass.getPassword()));
            if (r != null) { currentUserId = dao.getUserId(txtUser.getText());
                if ("CUSTOMER".equalsIgnoreCase(r)) cardLayout.show(mainPanel, "CUSTOMER");
                else if ("STAFF".equalsIgnoreCase(r)) cardLayout.show(mainPanel, "STAFF");
            } else JOptionPane.showMessageDialog(this, "Hatalı Giriş!");
        });
        btnRegister.addActionListener(e -> { returnToPanel = "LOGIN"; cardLayout.show(mainPanel, "REGISTER"); });
        return panel;
    }

    // --- 2. KAYIT (GÜNCELLENDİ: BUILDER DESENİ KULLANIMI) ---
    /**
     * Müşteri Kayıt Ekranı
     * Burada BUILDER Tasarım Deseni kullanılarak 'CustomerDTO' nesnesi oluşturulur.
     * Bu sayede çok parametreli (Ad, Soyad, TC, Tel...) veriler temiz bir şekilde paketlenir.
     */
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5);
        JTextField txtUser = new JTextField(15); JPasswordField txtPass = new JPasswordField(15); JTextField txtAd = new JTextField(15);
        JTextField txtSoyad = new JTextField(15); JTextField txtTC = new JTextField(15); JTextField txtEmail = new JTextField(15); JTextField txtTel = new JTextField(15);
        JButton btnSave = new JButton("Kaydet"); JButton btnBack = new JButton("Geri");
        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Kullanıcı:"), gbc); gbc.gridx=1; panel.add(txtUser, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Şifre:"), gbc); gbc.gridx=1; panel.add(txtPass, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Ad:"), gbc); gbc.gridx=1; panel.add(txtAd, gbc);
        gbc.gridx=0; gbc.gridy=3; panel.add(new JLabel("Soyad:"), gbc); gbc.gridx=1; panel.add(txtSoyad, gbc);
        gbc.gridx=0; gbc.gridy=4; panel.add(new JLabel("TC (11):"), gbc); gbc.gridx=1; panel.add(txtTC, gbc);
        gbc.gridx=0; gbc.gridy=5; panel.add(new JLabel("Email:"), gbc); gbc.gridx=1; panel.add(txtEmail, gbc);
        gbc.gridx=0; gbc.gridy=6; panel.add(new JLabel("Tel (11):"), gbc); gbc.gridx=1; panel.add(txtTel, gbc);
        gbc.gridx=1; gbc.gridy=7; panel.add(btnSave, gbc); gbc.gridx=1; gbc.gridy=8; panel.add(btnBack, gbc);

        btnSave.addActionListener(e -> {
            String tc = txtTC.getText().trim(), u = txtUser.getText().trim(), p = new String(txtPass.getPassword()).trim();
            if(u.isEmpty() || p.isEmpty() || tc.isEmpty()) { JOptionPane.showMessageDialog(this, "Eksik bilgi!"); return; }
            if(!tc.matches("\\d{11}")) { JOptionPane.showMessageDialog(this, "TC hatalı!"); return; }

            // --- BUILDER DESENİ KULLANIMI BAŞLANGICI ---
            // Zincirleme metotlarla (Fluent Interface) nesne oluşturuluyor.
            CustomerDTO newCustomer = new CustomerDTO.Builder(u, p, tc) // Zorunlu alanlar
                    .firstName(txtAd.getText()) // Opsiyonel alanlar
                    .lastName(txtSoyad.getText())
                    .phone(txtTel.getText())
                    .email(txtEmail.getText())
                    .build(); // Nesneyi inşa et (Build)
            // --- BUILDER DESENİ BİTİŞİ ---

            // Hazırlanan DTO nesnesini DAO'ya gönderiyoruz
            if(dao.registerCustomer(newCustomer)) {
                JOptionPane.showMessageDialog(this, "Kayıt Başarılı! (Builder Deseni ile)"); cardLayout.show(mainPanel, returnToPanel);
                txtUser.setText(""); txtPass.setText(""); txtAd.setText(""); txtSoyad.setText(""); txtTC.setText(""); txtEmail.setText(""); txtTel.setText("");
            } else JOptionPane.showMessageDialog(this, "Kayıt Hatası!");
        });
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, returnToPanel));
        return panel;
    }

    // --- 3. MÜŞTERİ PANELİ ---
    private JPanel createCustomerPanel() {
        JTabbedPane tabs = new JTabbedPane();
        // ODA ARA
        JPanel sP = new JPanel(new BorderLayout()); JPanel fP = new JPanel(new GridLayout(2, 4, 5, 5));
        JTextField tS = new JTextField("2025-12-20"); JTextField tE = new JTextField("2025-12-25");
        JComboBox<String> cT = new JComboBox<>(new String[]{"Standart", "Suit", "Aile"}); JSpinner sN = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        JButton bS = new JButton("Ara"); fP.add(new JLabel("Giriş:")); fP.add(tS); fP.add(new JLabel("Çıkış:")); fP.add(tE);
        fP.add(new JLabel("Tip:")); fP.add(cT); fP.add(new JLabel("Kişi:")); fP.add(sN);
        DefaultTableModel mM = new DefaultTableModel(new String[]{"Oda No", "Tip", "Fiyat", "Kapasite", "Durum"}, 0); JTable mT = new JTable(mM);
        JButton bB = new JButton("✅ Seçili Odayı Rezerve Et");
        bS.addActionListener(e -> { mM.setRowCount(0); List<Vector<Object>> r = dao.searchRoomsSmart(tS.getText(), tE.getText(), (int)sN.getValue(), (String) cT.getSelectedItem()); for (Vector<Object> row : r) mM.addRow(row); });

        bB.addActionListener(e -> {
            int row = mT.getSelectedRow(); if (row != -1) {
                String roomNo = (String) mM.getValueAt(row, 0);

                // --- STRATEGY DESENİ KULLANIMI ---
                // Müşteriler için varsayılan olarak 'NormalPricingStrategy' kullanılır.
                PricingStrategy strategy = new NormalPricingStrategy();

                // makeReservation metoduna bu stratejiyi parametre olarak geçiyoruz.
                if(dao.makeReservation(currentUserId, roomNo, tS.getText(), tE.getText(), (int)sN.getValue(), strategy)) {
                    // Observer Deseni: Bildirim gönder
                    notificationService.notifyAll("Müşteri Rezervasyonu (Normal Fiyat)!"); bS.doClick();
                } else JOptionPane.showMessageDialog(this, "Hata (Dolu/Kapasite)!");
            } else JOptionPane.showMessageDialog(this, "Oda seçin.");
        });
        JPanel tC = new JPanel(new BorderLayout()); tC.add(fP, BorderLayout.CENTER); tC.add(bS, BorderLayout.SOUTH);
        sP.add(tC, BorderLayout.NORTH); sP.add(new JScrollPane(mT), BorderLayout.CENTER); sP.add(bB, BorderLayout.SOUTH); tabs.addTab("Oda Ara", sP);

        // GEÇMİŞ ve PROFİL
        JPanel hP = new JPanel(new BorderLayout()); DefaultTableModel hM = new DefaultTableModel(new String[]{"ID", "Oda", "Giriş", "Çıkış", "Tutar", "Durum"}, 0); JTable hT = new JTable(hM); JButton hR = new JButton("Yenile"); JButton hC = new JButton("İptal Et"); JPanel hB = new JPanel(); hB.add(hR); hB.add(hC);
        hR.addActionListener(e -> { hM.setRowCount(0); Vector<Vector<Object>> d = dao.getCustomerReservations(currentUserId); for (Vector<Object> row : d) hM.addRow(row); });
        hC.addActionListener(e -> { int rw = hT.getSelectedRow(); if(rw!=-1) { if(dao.cancelReservation((int)hM.getValueAt(rw,0), (String)hM.getValueAt(rw,1))) { JOptionPane.showMessageDialog(this, "İptal Edildi."); hR.doClick(); }} else JOptionPane.showMessageDialog(this, "Seçim yapın."); });
        hP.add(new JScrollPane(hT), BorderLayout.CENTER); hP.add(hB, BorderLayout.SOUTH); tabs.addTab("Rezervasyonlarım", hP);
        JPanel pP = new JPanel(new GridBagLayout()); GridBagConstraints pg = new GridBagConstraints(); pg.insets = new Insets(5, 5, 5, 5); JTextField pe = new JTextField(15); JTextField pt = new JTextField(15); JPasswordField pp = new JPasswordField(15); JButton pl = new JButton("Getir"); JButton pu = new JButton("Güncelle");
        pg.gridx=0; pg.gridy=0; pP.add(new JLabel("Email:"), pg); pg.gridx=1; pP.add(pe, pg); pg.gridx=0; pg.gridy=1; pP.add(new JLabel("Tel:"), pg); pg.gridx=1; pP.add(pt, pg); pg.gridx=0; pg.gridy=2; pP.add(new JLabel("Şifre:"), pg); pg.gridx=1; pP.add(pp, pg); pg.gridx=0; pg.gridy=3; pP.add(pl, pg); pg.gridx=1; pP.add(pu, pg);
        pl.addActionListener(e -> { String[] i = dao.getCustomerProfile(currentUserId); if(i[0]!=null) { pe.setText(i[2]); pt.setText(i[3]); }}); pu.addActionListener(e -> { if(dao.updateCustomerProfile(currentUserId, pe.getText(), pt.getText(), new String(pp.getPassword()).trim())) JOptionPane.showMessageDialog(this, "Güncellendi!"); });
        tabs.addTab("Profilim", pP);
        JPanel mP = new JPanel(new BorderLayout()); JButton bL = new JButton("Çıkış"); bL.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN")); mP.add(tabs, BorderLayout.CENTER); mP.add(bL, BorderLayout.NORTH); return mP;
    }

    // --- 4. PERSONEL PANELİ (STRATEGY SEÇİMİ) ---
    /**
     * Personel Paneli
     * - Factory Deseni: Yeni oda eklerken 'RoomFactory' kullanır.
     * - Strategy Deseni: Rezervasyon yaparken 'İndirimli' veya 'Normal' fiyat seçimi sunar.
     */
    private JPanel createStaffPanel() {
        JTabbedPane tabs = new JTabbedPane();
        // ODA ve MÜŞTERİ YÖNETİMİ
        JPanel roomP = new JPanel(new BorderLayout()); JPanel addP = new JPanel(); JTextField rNo = new JTextField(5); JComboBox<String> rTip = new JComboBox<>(new String[]{"Standart", "Suit", "Aile"}); JTextField rFiyat = new JTextField("1000", 5); JSpinner rKap = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1)); JButton btnAdd = new JButton("Kaydet"); JButton btnUpdate = new JButton("Güncelle"); JButton btnListRooms = new JButton("Listele / Temizle");
        addP.add(new JLabel("No:")); addP.add(rNo); addP.add(new JLabel("Tip:")); addP.add(rTip); addP.add(new JLabel("Fiyat:")); addP.add(rFiyat); addP.add(new JLabel("Kapasite:")); addP.add(rKap); addP.add(btnAdd); addP.add(btnUpdate); DefaultTableModel rModel = new DefaultTableModel(new String[]{"No", "Tip", "Kapasite", "Fiyat", "Durum"}, 0); JTable rTable = new JTable(rModel);
        rTable.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent evt) { int row = rTable.getSelectedRow(); if (row != -1) { rNo.setText(rModel.getValueAt(row, 0).toString()); rTip.setSelectedItem(rModel.getValueAt(row, 1).toString()); rKap.setValue(Integer.parseInt(rModel.getValueAt(row, 2).toString())); rFiyat.setText(rModel.getValueAt(row, 3).toString()); rNo.setEditable(false); }}});

        // Factory Deseni Kullanımı
        btnAdd.addActionListener(e -> {
            if(dao.addRoomToDB(RoomFactory.createRoom((String)rTip.getSelectedItem(), rNo.getText()).getRoomNumber(), (String)rTip.getSelectedItem(), Double.parseDouble(rFiyat.getText()), (int)rKap.getValue())) {
                JOptionPane.showMessageDialog(this, "Eklendi!"); btnListRooms.doClick();
            } else JOptionPane.showMessageDialog(this, "Hata!");
        });

        btnUpdate.addActionListener(e -> { if(dao.updateRoom(rNo.getText(), (String)rTip.getSelectedItem(), Double.parseDouble(rFiyat.getText()), (int)rKap.getValue())) { JOptionPane.showMessageDialog(this, "Güncellendi!"); btnListRooms.doClick(); } else JOptionPane.showMessageDialog(this, "Hata!"); });
        btnListRooms.addActionListener(e -> { rModel.setRowCount(0); for (Vector<Object> row : dao.getAllRooms()) rModel.addRow(row); rNo.setText(""); rFiyat.setText("1000"); rNo.setEditable(true); });
        roomP.add(addP, BorderLayout.NORTH); roomP.add(new JScrollPane(rTable), BorderLayout.CENTER); roomP.add(btnListRooms, BorderLayout.SOUTH); tabs.addTab("Oda Yönetimi", roomP);
        JPanel custP = new JPanel(new BorderLayout()); JPanel custTop = new JPanel(); JTextField txtSearch = new JTextField(15); JButton btnSearchCust = new JButton("Ara"); JButton btnListAll = new JButton("Tümünü Listele"); JButton btnDetails = new JButton("📄 Detaylar"); JButton btnNewCust = new JButton("+ Yeni Müşteri");
        custTop.add(new JLabel("Ara:")); custTop.add(txtSearch); custTop.add(btnSearchCust); custTop.add(btnListAll); custTop.add(btnDetails); custTop.add(btnNewCust); DefaultTableModel cModel = new DefaultTableModel(new String[]{"ID", "Ad", "Soyad", "Tel", "Email", "TC"}, 0); JTable cTable = new JTable(cModel);
        btnSearchCust.addActionListener(e -> { cModel.setRowCount(0); for (Vector<Object> row : dao.searchCustomers(txtSearch.getText())) cModel.addRow(row); });
        btnListAll.addActionListener(e -> { cModel.setRowCount(0); for (Vector<Object> row : dao.searchCustomers("")) cModel.addRow(row); });
        btnDetails.addActionListener(e -> { int row = cTable.getSelectedRow(); if(row != -1) { Vector<Vector<Object>> history = dao.getCustomerReservations((int)cModel.getValueAt(row, 0)); JDialog dialog = new JDialog(this, "Geçmiş", true); dialog.setSize(600, 400); DefaultTableModel detModel = new DefaultTableModel(new String[]{"ID", "Oda", "Giriş", "Çıkış", "Tutar", "Durum"}, 0); for(Vector<Object> hRow : history) detModel.addRow(hRow); dialog.add(new JScrollPane(new JTable(detModel))); dialog.setLocationRelativeTo(this); dialog.setVisible(true); } else JOptionPane.showMessageDialog(this, "Seç!"); });
        btnNewCust.addActionListener(e -> { returnToPanel="STAFF"; cardLayout.show(mainPanel, "REGISTER"); JOptionPane.showMessageDialog(this, "Yönlendirildi."); });
        custP.add(custTop, BorderLayout.NORTH); custP.add(new JScrollPane(cTable), BorderLayout.CENTER); tabs.addTab("Müşteri Yönetimi", custP);

        // REZERVASYON YÖNETİMİ (STRATEGY SEÇİMİ )
        JPanel resP = new JPanel(new BorderLayout()); JPanel filterP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField fName = new JTextField(7); JTextField fRoom = new JTextField(5); JTextField fDateS = new JTextField(7); JTextField fDateE = new JTextField(7); JButton btnFilter = new JButton("🔍 Filtrele");
        JButton btnCheckIn = new JButton("Check-In"); JButton btnCheckOut = new JButton("Check-Out"); JButton btnCancel = new JButton("❌ İptal"); JButton btnNewRes = new JButton("+ TC ile Rezervasyon");
        filterP.add(new JLabel("Müş.:")); filterP.add(fName); filterP.add(new JLabel("Oda:")); filterP.add(fRoom); filterP.add(new JLabel("Tarih:")); filterP.add(fDateS); filterP.add(fDateE); filterP.add(btnFilter);
        DefaultTableModel resModel = new DefaultTableModel(new String[]{"ID", "Müşteri", "Oda(Tip)", "Giriş", "Çıkış", "Tutar", "Durum"}, 0); JTable resTable = new JTable(resModel); JPanel bottomP = new JPanel(); bottomP.add(btnCheckIn); bottomP.add(btnCheckOut); bottomP.add(btnCancel); bottomP.add(btnNewRes);
        btnFilter.addActionListener(e -> { resModel.setRowCount(0); for (Vector<Object> row : dao.getReservationsFiltered(fName.getText(), fRoom.getText(), fDateS.getText(), fDateE.getText())) resModel.addRow(row); });
        btnCheckIn.addActionListener(e -> { int row = resTable.getSelectedRow(); if(row!=-1) { dao.updateReservationStatus((int)resModel.getValueAt(row,0), "CHECK-IN YAPILDI"); btnFilter.doClick(); }});
        btnCheckOut.addActionListener(e -> { int row = resTable.getSelectedRow(); if(row!=-1) { dao.updateReservationStatus((int)resModel.getValueAt(row,0), "CHECK-OUT YAPILDI"); btnFilter.doClick(); }});
        btnCancel.addActionListener(e -> { int row = resTable.getSelectedRow(); if(row != -1 && JOptionPane.showConfirmDialog(this, "İptal?", "Onay", 0) == 0) { if(dao.cancelReservation((int)resModel.getValueAt(row, 0), ((String)resModel.getValueAt(row, 2)).split(" ")[0])) { JOptionPane.showMessageDialog(this, "İptal Edildi."); btnFilter.doClick(); }}});

        btnNewRes.addActionListener(e -> {
            String tcNo = JOptionPane.showInputDialog("TC:"); if(tcNo==null)return; int uId = dao.getUserId(tcNo); if(uId == 0) { JOptionPane.showMessageDialog(this, "Bulunamadı!"); return; }
            String room = JOptionPane.showInputDialog("Oda No:"); if(room==null)return; if(!dao.isRoomValid(room)) { JOptionPane.showMessageDialog(this, "Oda Yok!"); return; }
            String d1 = JOptionPane.showInputDialog("Giriş:"); if(d1==null)return; String d2 = JOptionPane.showInputDialog("Çıkış:"); if(d2==null)return;
            String gcStr = JOptionPane.showInputDialog("Kişi:"); if(gcStr==null)return; int gc = Integer.parseInt(gcStr);

            // --- STRATEGY DESENİ SEÇİMİ ---
            // Personel rezervasyon yaparken "İndirim yapılsın mı?" diye sorulur.
            // Cevaba göre farklı bir 'PricingStrategy' nesnesi (Normal veya İndirimli) seçilir.
            int choice = JOptionPane.showConfirmDialog(this, "Bu rezervasyona %20 İNDİRİM uygulansın mı?", "Fiyat Stratejisi (Strategy Pattern)", JOptionPane.YES_NO_OPTION);

            PricingStrategy strategy;
            if (choice == JOptionPane.YES_OPTION) {
                strategy = new DiscountPricingStrategy(); // İndirimli Strateji
            } else {
                strategy = new NormalPricingStrategy(); // Normal Strateji
            }

            // Seçilen strateji DAO'ya gönderilir ve fiyat buna göre hesaplanır.
            if(dao.makeReservation(uId, room, d1, d2, gc, strategy)) {
                JOptionPane.showMessageDialog(this, "Başarılı! (" + (choice==0 ? "İndirimli" : "Normal") + ")"); btnFilter.doClick();
            } else JOptionPane.showMessageDialog(this, "Hata (Dolu/Kapasite)!");
        });
        resP.add(filterP, BorderLayout.NORTH); resP.add(new JScrollPane(resTable), BorderLayout.CENTER); resP.add(bottomP, BorderLayout.SOUTH); tabs.addTab("Rezervasyon Yönetimi", resP);
        JPanel mainP = new JPanel(new BorderLayout()); JButton btnLogout = new JButton("Çıkış Yap"); btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN")); mainP.add(tabs, BorderLayout.CENTER); mainP.add(btnLogout, BorderLayout.NORTH); return mainP;
    }

    // Observer Deseni: Bildirim geldiğinde bu metot çalışır
    @Override public void update(String message) { JOptionPane.showMessageDialog(this, "BİLDİRİM (Observer): " + message); }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true)); }
}