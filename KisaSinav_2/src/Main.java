import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) throws IOException {
        // Görsellerin bilgisayarda bulunduğu dizini belirttim
        File dir = new File("C:\\Users\\HP\\Desktop\\KisaSinav_2\\Görseller");

        // Dizindeki .png uzantılı dosyaları alıyorum
        File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));

        // Dosyalardan BufferedImage listesi oluşturuyorum
        List<BufferedImage> images = new ArrayList<>();
        for (File file : files) {
            images.add(ImageIO.read(file));
        }

        // Görsel çiftlerini ve benzerliklerini tutacak bir liste oluşturuyorum
        List<ImagePair> pairs = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            for (int j = i + 1; j < images.size(); j++) {
                // İki görüntünün benzerliğini hesaplıyorum
                double benzerlik = getbenzerlik(images.get(i), images.get(j));

                // Görüntü çiftini ve benzerliğini ImagePair nesnesi olarak kaydediyoruz
                pairs.add(new ImagePair(files[i].getName(), files[j].getName(), benzerlik));
            }
        }

        // Benzerliklerine göre çiftleri sıralama
        Collections.sort(pairs, Comparator.comparing(ImagePair::getbenzerlik).reversed());

        // Çiftleri ekrana yazdırmak
        for (ImagePair pair : pairs) {
            System.out.println(pair);
        }
    }

    private static double getbenzerlik(BufferedImage img1, BufferedImage img2) {
        // Görüntülerin genişlik ve yükseklik bilgilerini almak
        int genislik1 = img1.getWidth();
        int yukseklik1 = img1.getHeight();
        int genislik2 = img2.getWidth();
        int yukseklik2 = img2.getHeight();

        // Görüntülerin aynı boyutta olup olmadığını kontrol ediyoruz değilse hata mesajı veriyoruz
        if (genislik1 != genislik2 || yukseklik1 != yukseklik2) {
            throw new IllegalArgumentException("Görsellerin aynı boyutta olması gerekir");
        }

        double benzerlik = 0;
        // Görüntüleri istenen ölçekte tarıyoruz
        for (int x = 0; x < genislik1; x += 20) {
            for (int y = 0; y < yukseklik1; y += 20) {
                // İlgili piksellerin RGB değerlerini alıyoruz
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;

                // Formülü uyguluyoruz
                benzerlik += 1 - Math.sqrt((r2 - r1) * (r2 - r1) + (g2 - g1) * (g2 - g1) + (b2 - b1) * (b2 - b1)) / Math.sqrt(255 * 255 + 255 * 255 + 255 * 255);
            }
        }

        // Toplam benzerliği, tarama bölgelerinin sayısına bölerek ortalama benzerliği hesaplıyoruz
        return benzerlik / ((genislik1 / 20) * (yukseklik1 / 20));
    }

    private static class ImagePair {
        private final String resim1;
        private final String resim2;
        private final double benzerlik;

        public ImagePair(String resim1, String resim2, double benzerlik) {
            this.resim1 = resim1;
            this.resim2 = resim2;
            this.benzerlik = benzerlik;
        }

        public double getbenzerlik() {
            return benzerlik;
        }

        @Override
        public String toString() {
            return resim1 + " - " + resim2 + ": " + benzerlik;
        }
    }
}