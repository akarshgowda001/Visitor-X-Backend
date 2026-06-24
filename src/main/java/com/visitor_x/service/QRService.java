

package com.visitor_x.service;

import com.google.zxing.WriterException;
import java.io.IOException;

public interface QRService {
    byte[] generateQRCode(String text) throws WriterException, IOException;
    String saveQRCode(String text) throws WriterException, IOException;
}