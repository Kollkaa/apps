package spring.boot;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Users {



    private String name;
    private Long chatid;
    private ArrayList<Sticker> stickers;

    public void setZakaz(int zakaz) {
        this.zakaz = zakaz;
    }

    private  int zakaz=0;
    private String screenName;
    static BufferedImage sample;
    static BufferedImage finalImg;
    BufferedImage temp;

    public Users(Long chatid, ArrayList<Sticker> stickers,String name) {
        this.chatid = chatid;
        this.name=name;
        this.stickers = stickers;
        this.screenName="src/main/resources/"+this.chatid+"/";
        this.preview=new SendPhoto();
        this.preview.setChatId(chatid).setCaption(chatid.toString());
        try
        {
            sample = ImageIO.read(new File("src/main/resources/" + "tamplate.png"));

        } catch(
                IOException e)

        {
            e.printStackTrace();
        }

        finalImg = new BufferedImage(sample.getWidth() * 1, sample.getHeight() * 1, sample.getType());

        try

        {
            temp = ImageIO.read(new File("src/main/resources/StickerPackImage.png"));
        } catch(
                IOException e)

        {
            e.printStackTrace();
        }
        finalImg.createGraphics().

                drawImage(temp, sample.getWidth()/3,sample.getHeight()-130,null);

    }

    public SendPhoto getPreview() throws IOException {


        return preview;
    }

    public void setPreview(SendPhoto preview) {
        this.preview = preview;
    }

    private SendPhoto preview;

    private int x=35;
    private int y=0;

    private int count=0;
    public String getScreenName() {
        return screenName;
    }
    // Add new Sticker for Sticker pack
    public  void AddPhotoToTemplate() throws IOException {

        File final_Image = new File(this.screenName+"stickerpack.png");
        finalImg = new BufferedImage(sample.getWidth() * 1, sample.getHeight() * 1, sample.getType());
        for(int i=1;i<=getCount();i++){
            int wid=sample.getWidth()/3;
            int hei=sample.getWidth()/4;
            try {
                temp = ImageIO.read(new File(this.screenName+"sticker"+i+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(x+" "+y);
            if(i%3==0)
            {
                finalImg.createGraphics().drawImage(temp, getX(),getY() , null);
                setX(35);
                setY(getY()+hei+150);
            }else{

                finalImg.createGraphics().drawImage(temp, getX(),getY() , null);
                setX(getX()+wid);






            }


        }
        temp = ImageIO.read(new File("src/main/resources/StickerPackImage.png"));

        finalImg.createGraphics().

                drawImage(temp, sample.getWidth()/3,sample.getHeight()-130,null);

        try {
            ImageIO.write(finalImg, "png", final_Image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreview(new SendPhoto().setChatId(getChatid()).setPhoto(new File(this.screenName+"stickerpack.png")));

    }
    // Send finished result packSticker
    public SendPhoto combineALLImages() throws IOException, InterruptedException {




        File final_Image = new File(getScreenName()+"stickerpack.png");
        ImageIO.write(finalImg, "png", final_Image);


        return new SendPhoto().setChatId(getChatid()).setPhoto(new File(getScreenName()+"stickerpack.png"));
    }

    public  void Rebuild(){
        try {
            File f=new File("src/main/resources/"+getChatid()+"/stickerpack.png");
            File fe=new File("src/main/resources/"+getChatid());
            try { System.out.println(f.delete());
                System.out.println(fe.delete()); }
            catch (Exception e) {
                e.printStackTrace();
            }
            sample = ImageIO.read(new File("src/main/resources/" + "tamplate.png"));
            finalImg = new BufferedImage(sample.getWidth() * 1, sample.getHeight() * 1, sample.getType());
            temp = ImageIO.read(new File("src/main/resources/" + "StickerPackImage.png"));
            finalImg.createGraphics().
                    drawImage(temp, sample.getWidth() / 3, sample.getHeight() - 130, null);
            setPreview(new SendPhoto());

        }
        catch (Exception e)
        {System.out.println(e.getStackTrace());}
        setStickers( new ArrayList<Sticker>());

        setCount(0);
        returnes_x_y();

    }

    public void returnes_x_y()
    {
        x=35;
        y=0;

    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getName() {
        return name;
    }

    public  int getZakaz() {
        return zakaz;
    }

    public void setZakaz() {
        this.zakaz+=1;
    }



    public Long getChatid() {
        return chatid;
    }

    public void setChatid(Long chatid) {
        this.chatid = chatid;
    }

    public ArrayList<Sticker> getStickers() {
        return stickers;
    }
    public void AddSticker(Sticker sticker)
    {
        stickers.add(sticker);
    }

    public void setStickers(ArrayList<Sticker> stickers) {
        this.stickers = stickers;
    }
}
