/**
*
* @author Ensar CELİK ensar.celik2@ogr.sakarya.edu.tr
* @since 02.04.2023
* <p>
* Satirlar okunur, istenilen regexler aranir, bulunan regexlerde ilgili islemler yapilir, Satirlar yazdirilir.
* </p>
*/
package pdp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.tools.javac.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;

public class pdp {

	public static void main(String[] args) throws IOException {		
		String dosyaadi = args[0];             //Konsoldan parametre olarak *.java dosyasi girilir. ornek: "java -jar odev.jar ../icerik.java
		String tekSatirDosya = "teksatir.txt";  //Tek satir yorumlarin  yazilacagi text dosyasi
		String cokSatirDosya = "coksatir.txt";	//Cok satir yorumlarin yazilacagi text dosyasi
		String javaSatirDosya = "javadoc.txt";	//Javadoc yorumlarin yazilacagi text dosyasi
		
		ArrayList<String> esleyenMetod = new ArrayList<String>();	      //METOTLARI BU DIZIYE ATIYORUM
		ArrayList<String> esleyenConstructor = new ArrayList<String>();   //YAPICI METOTLARI BU DIZIYE ATIYORUM
		ArrayList<String> esleyenClass = new ArrayList<String>();         //SINIFLARI BU DIZIYE ATIYORUM
		ArrayList<String> esleyenCokluYorum = new ArrayList<String>();    //COKLU YORUMLARI BU DIZIYE ATIYORUM
		ArrayList<String> esleyenJavaYorum = new ArrayList<String>();	  //JAVADOC YORUMLARINI BU DIZIYE ATIYORUM
		ArrayList<String> esleyenTekSatirYorum = new ArrayList<String>(); //TEK SATIR YORUMLARI BU DIZIYE ATIYORUM
		
		
		String satirlar = null;		//Okunan satirlar bu string e atilacak.
		int tekliSatirSayisi = 0;  //Tek satir yorum sayisi
		int cokluSatirSayisi = 0;  //Coklu satir yorum sayisi
		int javaSatirSayisi = 0;   //Javadoc yorum sayisi
		int SusluParantez = 0;	 //Bu 0'dan farkli oldugunda metot icerisindeyiz demektir
		int javaToggle = 0;      //Bu 0'dan farkli oldugunda javadoc yorumu  icerisindeyiz demektir.
		int cokluToggle = 0;	//Bu 0'dan farkli oldugunda coklu satir yorum  icerisindeyiz demektir.
		int yorumAramaIzni = 1;  //Bir yorum basladiginda  icinde "//" ile baska bir yorum daha baslayabilir. Bu durumda  icerideki yorumu ekstra saymamak icin bir izin koydum.
								//yani kirmizi isik gibi. 1 ise yesil yaniyor yani arayabilirsin. 1den farkli ise kirmizi yaniyor yeni bir yorum sayamazsin. Yani gordugun sey yorum icinde yorum.
		
		Pattern yolMetod = Pattern.compile("(?<=([Ss]tring)|(int)|(void)|(double)|(bool)|(ostream&)|(~)|\\})(.*)(?=\\()");
		Pattern yolConstructor = Pattern.compile("(?<=(public ))(\\w*)(?=\\()");
		Pattern yolClass = Pattern.compile("(?<=(class )).*(?=\\{)");
		Pattern yolCokluYorum = Pattern.compile("\\/\\*[^*]");       // /* yorum */ yorumlarini yazdirmak icin
		Pattern yolCokluYorumKapanis = Pattern.compile("\\*\\/");
		Pattern yolJavaYorum = Pattern.compile("\\/\\*\\*");
		Pattern yolTekSatirYorum = Pattern.compile("\\/\\/[^\\n]*");
		Pattern yolMetotBasi = Pattern.compile("\\{");  //Fonksiyon gordugunde acilan { karakterlerini saymaya baslayacak.
		Pattern yolMetotSonu = Pattern.compile("}");    //Fonksiyon gordukten sonra kapanan } karakterlerini saymaya baslayacak.
		Pattern yolCokluTekSatirYorum = Pattern.compile("(?<=\\/\\*\\s*).*(?=\\*\\/)");  // Öncesinde /* olan sonrasında */ olan karakterleri al yani tek 1 satırdaki yorumu
		
		
try {
        	
        	//dosya okuma islemleri
         
            FileReader dosyayioku = new FileReader(dosyaadi);            
            FileWriter tekliDosyayaYaz = new FileWriter(tekSatirDosya);
            FileWriter cokluDosyayaYaz = new FileWriter(cokSatirDosya);
            FileWriter javadocDosyayaYaz = new FileWriter(javaSatirDosya);
            BufferedWriter bw1 = new BufferedWriter(tekliDosyayaYaz);
            BufferedWriter bw2 = new BufferedWriter(cokluDosyayaYaz);
            BufferedWriter bw3 = new BufferedWriter(javadocDosyayaYaz);
            BufferedReader okuyucu = new BufferedReader(dosyayioku);
            
            while ((satirlar = okuyucu.readLine()) != null) {     //DOSYA SATIR SATIR OKUNUYOR. NULL OLDUGUNDA DURUYOR.
            	
            	for(int i =0; i<satirlar.length(); i++) {     //HER SATIRIN SONUNA 1 TANE BOSLUK ATIYORUM cunku /* ile biten satirda 2.yildizin olup olmadigini kontrol 
            		if(i==satirlar.length()-1) {              // etmem icin 3. bir karaktere ihtiyacim var. /* dan sonra bosluk olursa kontrolu yapabiliyorum.
            			satirlar = satirlar + " ";            			
            			break;
            		}
            	}
            	
            	//OKUNAN SATIRLAR REGEX IFADELER ILE ESLESIYOR
            	Matcher esleMetod = yolMetod.matcher(satirlar);            	     
            	Matcher esleConstructor = yolConstructor.matcher(satirlar);
            	Matcher esleClass = yolClass.matcher(satirlar);
            	Matcher esleCokluYorum = yolCokluYorum.matcher(satirlar);
            	Matcher esleCokluYorumKapanis = yolCokluYorumKapanis.matcher(satirlar);    
            	Matcher esleJavaYorum = yolJavaYorum.matcher(satirlar);
            	Matcher esleTekSatirYorum = yolTekSatirYorum.matcher(satirlar);
            	Matcher esleMetotBasi = yolMetotBasi.matcher(satirlar);  
            	Matcher esleMetotSonu = yolMetotSonu.matcher(satirlar);
            	Matcher esleCokluTekSatirYorum = yolCokluTekSatirYorum.matcher(satirlar);
            	
            	
            	
            	
            	//Sinif bulma regexi ile satir eslestiyse burasi calisir.
            	while (esleClass.find()) {
            		String gecici = esleClass.group().replaceAll("[^A-Za-z0-9]+", "");  //Sinif etrafindaki gereksiz yerler silinir.
            		if(gecici.length()!=0) {                   //Eger gecici varsa burasi calisir
            			if(!esleyenClass.contains(gecici)) {
            				esleyenClass.add(gecici);
            				System.out.println("Sinif: "+gecici);
            			}
            		}
            	}
            	//Metot bulma regexi ile satir eslestiyse burasi calisir.
            	while (esleMetod.find()) {
            		String gecici = esleMetod.group().replaceAll("[^A-Za-z0-9]+", ""); //Metot etrafindaki gereksiz yerler silinir.
            		if (gecici.length() != 0) {              //Eger gecici varsa burasi calisir      	                  	 
                      if (!esleyenMetod.contains(gecici)) {						   
                          esleyenMetod.add(gecici);
                          System.out.println("\tFonksiyon: "+gecici);
                          bw1.write("Fonksiyon: "+gecici+"\n\n");		//Tek Satir dosyasinin icine fonksiyon yazilir.
                          bw2.write("Fonksiyon: "+gecici+"\n\n");		//Cok Satir dosyasinin icine fonksiyon yazilir.
                          bw3.write("Fonksiyon: "+gecici+"\n\n");		//Javadoc dosyasinin icine fonksiyon yazilir.
                          if(esleMetotBasi.find())     //Metot gorunce { gordugunde SusluParantez sayisini artir.
                        	  SusluParantez++;
                      }
                                          
                  }
            	}
            	if(SusluParantez!=0) {            //Susluparantez basta 0. Yani basta bu if calismaz
            		while(esleMetotBasi.find()){  //Yukardan metot buldugu zaman SusluParantez 0dan cikar ve if calisir. { ve } karakterleri sayilmaya baslanir.
            			SusluParantez++;
            		}
            		while(esleMetotSonu.find()) { // } karakterigoruldugunde buraya girilir.
            			SusluParantez--;		  //Susluparantez sayisi eksiltilir.
            			if(SusluParantez==0) {    //Susluparantez 0 ise metot sonuna geldik demektir. Yazdirmaya baslayabiliriz.
            				System.out.println("\t\tTek Satır Yorum sayısı:    "+tekliSatirSayisi);    //Burada yorum satiri sayilari konsola yazdirilir.
                    		System.out.println("\t\tÇok Satırlı Yorum sayısı:  "+(cokluSatirSayisi));
                    		System.out.println("\t\tJavadoc Yorum sayısı:      "+javaSatirSayisi);
                    		tekliSatirSayisi=0;		//butun yorum sayilari sifirlanir.
                    		cokluSatirSayisi=0;
                    		javaSatirSayisi=0;
                    		System.out.println("--------------------------------------------");                    		
                    		if(esleyenTekSatirYorum.size()==0)		//Eger hic tek satir yorum yoksa burasi calisir.
                    			bw1.write("(Tek satir yorum yok)\n");
                    		while(0<esleyenTekSatirYorum.size()) {  //Tek satir yorum var ise burasi calisir.
                    			bw1.write(esleyenTekSatirYorum.get(0)+"\n");  //Tek Satirlara attigimiz dizinin ilk elemani ilgili metin dosyasina yazdirilir.
                    			esleyenTekSatirYorum.remove(0);  //Yazilan tek satir yorum diziden kaldirilir.
                    		}                    		
                    		bw1.write("\n-------------------------\n\n");
                    		if(esleyenCokluYorum.size()==0)   //Coklu satir yok ise burasi calisir
                    			bw2.write("(Coklu satir yorum yok)\n");
                    		while(0<esleyenCokluYorum.size()) {  //Coklu satir yorum var ise burasi calisir
                    			bw2.write(esleyenCokluYorum.get(0)+"\n");  //Cok Satirlara attigimiz dizinin ilk elemani ilgili metin dosyasina yazdirilir.
                    			esleyenCokluYorum.remove(0); //Yazilan cok satir yorum diziden kaldirilir.
                    		}
                    		bw2.write("\n-------------------------\n\n");
                    		if(esleyenJavaYorum.size()==0)       //Javadoc yorum yok ise burasi calisir
                    			bw3.write(" (Javadoc yorum yok)\n");
                    		while(0<esleyenJavaYorum.size()) {   //Javadoc yorum var ise burasi calisir
                				bw3.write(esleyenJavaYorum.get(0)+"\n"); //javadoc yorumlari attigimiz dizinin ilk elemani ilgili metin dosyasina yazdirilir.
                				esleyenJavaYorum.remove(0);      //Yazilan javadoc yorum diziden kaldirilir.
                			}
                    		bw3.write("\n-------------------------\n\n");
                    		
            			}
            		}
            	}
            	if(javaToggle!=0) {            		//Java yorum Satiri basladiysa burasi 1 olur ve calisir.
            		while(esleCokluYorumKapanis.find()) {  //java yorum kapanis "*/" bulunursa burasi calisir.
            			javaToggle--;  //javaToggle 1 azaltilir. Boylece 0 oldugunda java yorum bitmis olur.
            			yorumAramaIzni--;
            		}
            		if(javaToggle!=0) {  //1 azaltildiginda toggle 0 olmadiysa burasi calisir.
            			String gecici = satirlar.replaceAll("^(\\s*)\\*", "");  //java yorumlari basindaki * kaldirilarak gecici degiskene atilir.
                		esleyenJavaYorum.add(gecici);  //Bu gecici degisken diziye atilir. Bu dizi de yukarida bw3 ile javadoc metin dosyasina yazdirilir.			
            		}
            	}
            	if(cokluToggle!=0) {
            		while(esleCokluYorumKapanis.find()) {
            			cokluToggle--;
            			yorumAramaIzni--;
            		}
            		if(cokluToggle!=0) {
            			String gecici = satirlar.replaceAll("^(\\s*)\\*", "");
            			esleyenCokluYorum.add(gecici);
            		}
            	}
            	//Yapici metot bulma regexi ile satir eslestiyse burasi calisir.
            	while (esleConstructor.find()) {   
                    String gecici = esleConstructor.group().replaceAll("[^A-Za-z0-9]+", " ");
                    
                    //eger gecici'nin uzunlugundan buyukse yani gecici varsa                     
                    if (gecici.length() != 0) {
                        if (true) {                       	                             
                        	esleyenConstructor.add(gecici);
                        	System.out.println("\tFonksiyon: "+gecici);
                        	bw1.write("Fonksiyon: "+gecici+"\n\n");     //Yapici fonksiyonlar tek tek ilgili metin dosyalarina yazdirilir.
                        	bw2.write("Fonksiyon: "+gecici+"\n\n");
                        	bw3.write("Fonksiyon: "+gecici+"\n\n");
                            if(esleMetotBasi.find())   //Yapici metot gorunce { gordugunde SusluParantez sayisini artir.
                            	SusluParantez++;
                        }
                    }                                     
                }
            	if(yorumAramaIzni==1) {
            	//Tek satir yorum bulma regexi ile satir eslestiginde calisir
            	while (esleTekSatirYorum.find()) {             		
            		String gecici = esleTekSatirYorum.group().replaceAll("\\/\\/\\s?", "");            		
            		if (gecici.length() != 0) {                     					   
                          esleyenTekSatirYorum.add(gecici);
                          tekliSatirSayisi++;
                  }
            	}
            	}
            	if(yorumAramaIzni==1) {
            	//Cok satir yorum bulma regexi ile satir eslestiginde calisir
            	while (esleCokluYorum.find()) {
            		yorumAramaIzni++;            		                                                              
                    cokluSatirSayisi++; 
                    cokluToggle++;
                    while(esleCokluYorumKapanis.find()) {    //Bu calisirsa demek ki /* yorum */ tarzinda bir yorum okuyoruz demektir.
                        while(esleCokluTekSatirYorum.find()) {
                        	String gecici = esleCokluTekSatirYorum.group().replaceAll("", "");         		                   	                  	 						   
                            esleyenCokluYorum.add(gecici);
                      		cokluToggle--;
                      		yorumAramaIzni--;
                        }
                        	  
                  	}
            	}
            	}
            	if(yorumAramaIzni==1) {
            	//Javadoc yorum bulma regexi ile satir eslestiginde calisir
            	while (esleJavaYorum.find()) {
            		//String gecici = satirlar;
            		javaToggle++;
            		yorumAramaIzni++;            		
                    javaSatirSayisi++;                 
            	}
            	}            	
            	
            	
            }
            //Acilan her dosya kapatilir.
            okuyucu.close();
            bw1.close();
            bw2.close();
            bw3.close();
}
catch (FileNotFoundException ex) {
    System.out.println("Dosya bulunamadi.");
} catch (IOException ex) {
    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
}
		
		
			
		
		
	}

}
