package com.example.shopflow.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.shopflow.entity.*;
import com.example.shopflow.enums.*;
import com.example.shopflow.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final CartRepository cartRepo;
    private final CouponRepository couponRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository userRepo,
                           CategoryRepository categoryRepo,
                           ProductRepository productRepo,
                           CartRepository cartRepo,
                           CouponRepository couponRepo,
                           PasswordEncoder encoder) {
        this.userRepo      = userRepo;
        this.categoryRepo  = categoryRepo;
        this.productRepo   = productRepo;
        this.cartRepo      = cartRepo;
        this.couponRepo    = couponRepo;
        this.encoder       = encoder;
    }

    @Override
    public void run(String... args) {

        // ── Admin ──────────────────────────────────────────
        // Ne pas réinsérer si les données existent déjà
        if (userRepo.existsByEmail("admin@shopflow.tn")) {
            System.out.println("ℹ️  Données déjà présentes — initialisation ignorée");
            return;
        }
        User admin = new User();
        admin.setPrenom("Admin");
        admin.setNom("ShopFlow");
        admin.setEmail("admin@shopflow.tn");
        admin.setMotDePasse(encoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setActif(true);
        userRepo.save(admin);

        // ── Seller ─────────────────────────────────────────
        User seller = new User();
        seller.setPrenom("Mohamed");
        seller.setNom("Ben Ali");
        seller.setEmail("seller@shopflow.tn");
        seller.setMotDePasse(encoder.encode("seller123"));
        seller.setRole(Role.SELLER);
        seller.setActif(true);
        userRepo.save(seller);

        SellerProfile profile = new SellerProfile();
        profile.setUser(seller);
        profile.setNomBoutique("Tech Store TN");
        profile.setDescription("Boutique spécialisée en électronique");
        seller.setSellerProfile(profile);
        userRepo.save(seller);

        // ── Customer ───────────────────────────────────────
        User customer = new User();
        customer.setPrenom("Fatma");
        customer.setNom("Khalil");
        customer.setEmail("customer@shopflow.tn");
        customer.setMotDePasse(encoder.encode("customer123"));
        customer.setRole(Role.CUSTOMER);
        customer.setActif(true);
        userRepo.save(customer);

        // Panier du customer
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepo.save(cart);

        // Adresse du customer
        Address address = new Address();
        address.setUser(customer);
        address.setRue("12 Rue de la Liberté");
        address.setVille("Tunis");
        address.setCodePostal("1001");
        address.setPays("Tunisie");
        address.setPrincipal(true);
        customer.getAddresses().add(address);
        userRepo.save(customer);

        // ── Catégories ─────────────────────────────────────
        Category electronique = new Category();
        electronique.setNom("Électronique");
        electronique.setDescription("Produits électroniques");
        categoryRepo.save(electronique);

        Category smartphones = new Category();
        smartphones.setNom("Smartphones");
        smartphones.setDescription("Téléphones mobiles");
        smartphones.setParent(electronique);
        categoryRepo.save(smartphones);

        Category informatique = new Category();
        informatique.setNom("Informatique");
        informatique.setDescription("PC, laptops, accessoires");
        informatique.setParent(electronique);
        categoryRepo.save(informatique);

        // ── Produits ───────────────────────────────────────
        Product iphone = new Product();
        iphone.setNom("iPhone 15 Pro");
        iphone.setDescription("Dernier iPhone d'Apple avec puce A17 Pro");
        iphone.setPrix(new BigDecimal("3499.00"));
        iphone.setPrixPromo(new BigDecimal("3199.00"));
        iphone.setStock(50);
        iphone.setSeller(seller);
        iphone.setImageUrl("https://images.apple.com/iphone-15-pro.jpg");
        iphone.getCategories().add(smartphones);
        productRepo.save(iphone);

        Product laptop = new Product();
        laptop.setNom("Dell XPS 15");
        laptop.setDescription("Laptop premium avec écran OLED 15 pouces");
        laptop.setPrix(new BigDecimal("5200.00"));
        laptop.setStock(20);
        laptop.setSeller(seller);
        laptop.setImageUrl("https://images.dell.com/xps15.jpg");
        laptop.getCategories().add(informatique);
        productRepo.save(laptop);

        Product samsung = new Product();
        samsung.setNom("Samsung Galaxy S24");
        samsung.setDescription("Flagship Samsung avec IA intégrée");
        samsung.setPrix(new BigDecimal("2799.00"));
        samsung.setPrixPromo(new BigDecimal("2499.00"));
        samsung.setStock(35);
        samsung.setSeller(seller);
        samsung.getCategories().add(smartphones);
        productRepo.save(samsung);

        // ── Coupons ────────────────────────────────────────
        Coupon coupon1 = new Coupon();
        coupon1.setCode("GLID2025");
        coupon1.setType(CouponType.PERCENT);
        coupon1.setValeur(new BigDecimal("10"));
        coupon1.setDateExpiration(LocalDate.now().plusMonths(6));
        coupon1.setUsagesMax(100);
        coupon1.setActif(true);
        couponRepo.save(coupon1);

        Coupon coupon2 = new Coupon();
        coupon2.setCode("BIENVENUE");
        coupon2.setType(CouponType.FIXED);
        coupon2.setValeur(new BigDecimal("50"));
        coupon2.setDateExpiration(LocalDate.now().plusMonths(3));
        coupon2.setUsagesMax(50);
        coupon2.setActif(true);
        couponRepo.save(coupon2);

        // ── Log ────────────────────────────────────────────
        System.out.println("================================================");
        System.out.println("✅  Données de test chargées !");
        System.out.println("    admin@shopflow.tn    / admin123");
        System.out.println("    seller@shopflow.tn   / seller123");
        System.out.println("    customer@shopflow.tn / customer123");
        System.out.println("    Swagger : http://localhost:8081/swagger-ui.html");
        //System.out.println("    H2      : http://localhost:8081/h2-console");
        System.out.println("================================================");
    }
}