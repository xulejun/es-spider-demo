package com.xlj.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

/**
 * zxing-3.4.1 解析二维码测试
 * 经测试：
 *      1. 二维码可以解析获得内容地址
 *      2. 小程序码不能解析
 *
 * @author xlj
 * @date 2021/4/15
 */
@Slf4j
public class QrCodeParseTest {

    @Test
    public void testParse() throws Exception {
        String url = "https://mmbiz.qpic.cn/mmbiz_jpg/N6rM2Jp5QdiaBxC4ygWxbZPJ6cpib0PUycKyZH8sudQ0UF46NgUXQibGicbGSm9lyjeQnEx9ibLG7Q3BnMMbEcGnVGA/640?wx_fmt=jpeg";
        parse(url);
    }

    /**
     * 二维码解析
     * @param url
     * @throws Exception
     */
    public static void parse(String url) throws Exception {
        URL urlFile = new URL(url);
        InputStream openStream = urlFile.openStream();
        BufferedImage bufferedImage = ImageIO.read(openStream);

        if (bufferedImage != null) {
            LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
            HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            QRCodeReader qrCodeReader = new QRCodeReader();

            // 二维码解析
            Result decode = qrCodeReader.decode(binaryBitmap);
            String text = decode.getText();
            log.info("解析二维码的内容为：{}", text);
        }
    }
}
