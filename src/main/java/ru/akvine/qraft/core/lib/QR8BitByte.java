package ru.akvine.qraft.core.lib;

import java.io.UnsupportedEncodingException;

public class QR8BitByte extends QRData {

    public QR8BitByte(String data) {
        super(Mode.MODE_8BIT_BYTE, data);
    }

    public void write(BitBuffer buffer) {

        try {

            byte[] data = getData().getBytes(QRCode.get8BitByteEncoding() );

            for (int i = 0; i < data.length; i++) {
                buffer.put(data[i], 8);
            }

        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage() );
        }
    }

    public int getLength() {
        try {
            return getData().getBytes(QRCode.get8BitByteEncoding() ).length;
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage() );
        }
    }
}
