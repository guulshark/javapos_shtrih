package com.shtrih.fiscalprinter.scoc.commands;

import com.shtrih.fiscalprinter.scoc.commands.ScocCommand;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.shtrih.fiscalprinter.ByteUtils.byteArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ScocCommandTests {
    @Test
    public void Should_build_command() throws Exception {
        byte[] expectedData = byteArray(
                0x06, 0xDF, 0x09, 0x42, // Сигнатура
                0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x31, 0x31, 0x31, 0x31,
                0x30, 0x30, 0x32, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, // Номер ККМ
                0x30, 0x31, 0x30, 0x31, // Версия S-протокола
                0x30, 0x31, 0x30, 0x31, // Версия А-протокола
                0x3D, 0x00, // Размер тела
                0x40, 0xE2, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, // UIN
                0x00, 0x00, // Флаги
                0x00, 0x00, // Проверочный код
                0x47, 0x1F, // 8007
                0x39, 0x00, // 57
                0x08, 0x20, 0x04, 0x00, 0x01, 0x00,
                0x00, 0x00, 0x58, 0x1B, 0x04, 0x00, 0x51, 0xB6, 0x2C, 0x5B,
                0x1F, 0x20, 0x25, 0x00, 0x1B, 0x20, 0x04, 0x00, 0xA8, 0xDC,
                0x8D, 0x59, 0x72, 0x20, 0x02, 0x00, 0x00, 0x00, 0x73, 0x20,
                0x00, 0x00, 0x76, 0x20, 0x00, 0x00, 0x74, 0x20, 0x01, 0x00,
                0x00, 0x75, 0x20, 0x01, 0x00, 0x00, 0x78, 0x20, 0x01, 0x00,
                0x00);

        byte[] data = byteArray(
                0x47, 0x1F, 0x39, 0x00, 0x08, 0x20, 0x04, 0x00, 0x01, 0x00,
                0x00, 0x00, 0x58, 0x1B, 0x04, 0x00, 0x51, 0xB6, 0x2C, 0x5B,
                0x1F, 0x20, 0x25, 0x00, 0x1B, 0x20, 0x04, 0x00, 0xA8, 0xDC,
                0x8D, 0x59, 0x72, 0x20, 0x02, 0x00, 0x00, 0x00, 0x73, 0x20,
                0x00, 0x00, 0x76, 0x20, 0x00, 0x00, 0x74, 0x20, 0x01, 0x00,
                0x00, 0x75, 0x20, 0x01, 0x00, 0x00, 0x78, 0x20, 0x01, 0x00,
                0x00);

        ScocCommand cmd = new ScocCommand("00000111110020123456", 123456L, data);

        assertArrayEquals(expectedData, cmd.toBytes());
    }

    @Test
    public void Should_read_from_input_stream() throws Exception {
        byte[] data = byteArray(
                0x06, 0xDF, 0x09, 0x42, // Сигнатура
                0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x31, 0x31, 0x31, 0x31,
                0x30, 0x30, 0x32, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36,
                0x30, 0x31, 0x30, 0x31, 0x30, 0x31, 0x30, 0x31, 0x17, 0x00,
                0x40, 0xE2, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0xA5, 0x1F, 0x13, 0x00, 0x15, 0x20, 0x01, 0x00,
                0x00, 0x17, 0x20, 0x02, 0x00, 0x08, 0x00, 0x08, 0x20, 0x04,
                0x00, 0x01, 0x00, 0x00, 0x00);


        InputStream inputStream = new ByteArrayInputStream(data);
        ScocCommand cmd = ScocCommand.read(inputStream);

        assertEquals("00000111110020123456", cmd.getSerialNumber());
        assertEquals(123456L, cmd.getUin());

        assertArrayEquals(byteArray(0xA5, 0x1F, 0x13, 0x00, 0x15, 0x20, 0x01, 0x00,
                0x00, 0x17, 0x20, 0x02, 0x00, 0x08, 0x00, 0x08, 0x20, 0x04,
                0x00, 0x01, 0x00, 0x00, 0x00), cmd.getData());
    }
}

