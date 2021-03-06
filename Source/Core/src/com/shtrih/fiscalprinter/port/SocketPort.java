package com.shtrih.fiscalprinter.port;

import java.io.*;
import java.net.*;
import java.util.*;
import com.shtrih.util.*;

/**
 * @author V.Kravtsov
 */
public class SocketPort implements PrinterPort 
{
    private static CompositeLogger logger = CompositeLogger.getLogger(SocketPort.class);
    private static Map<String, SocketPort> items = new HashMap<String, SocketPort>();

    private Socket socket = null;
    private final String portName;
    private int readTimeout;
    private int openTimeout;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public static synchronized SocketPort getInstance(String portName, int openTimeout) {
        SocketPort item = items.get(portName);
        if (item == null) {
            item = new SocketPort(portName, openTimeout);
            items.put(portName, item);
        }
        return item;
    }

    private SocketPort(String portName, int openTimeout) {
        this.portName = portName;
        this.openTimeout = openTimeout;
        this.readTimeout = openTimeout;
        logger.debug("SocketPort(" + portName + ")");
    }

    public void open() throws Exception {
        open(openTimeout);
    }

    private boolean isConnected() {
        return socket != null;
    }

    public void open(int timeout) throws Exception {
        if (isConnected()) {
            return;
        }

        socket = new Socket();
        socket.setReuseAddress(true);
        socket.setSoTimeout(readTimeout);
        socket.setTcpNoDelay(true);

        StringTokenizer tokenizer = new StringTokenizer(portName, ":");
        String host = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        socket.connect(new InetSocketAddress(host, port), openTimeout);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public void close() {
        if (!isConnected()) {
            return;
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            Thread.sleep(100);
        } catch (Exception e) {
        }
        socket = null;
        inputStream = null;
        outputStream = null;
    }

    public int readByte() throws Exception {
        open();

        int b = inputStream.read();
        if (b == -1) {
            noConnectionError();
        }
        
        return b;
    }

    private void noConnectionError() throws Exception {
        throw new IOException(Localizer.getString(Localizer.NoConnection));
    }

    public byte[] readBytes(int len) throws Exception {
        open();

        byte[] data = new byte[len];
        int offset = 0;
        while (len > 0) {
            int count = inputStream.read(data, offset, len);
            if (count == -1) {
                noConnectionError();
            }
            len -= count;
            offset += count;
        }
        return data;
    }

    public void write(byte[] b) throws Exception {
        for (int i = 0; i < 2; i++) {
            try {
                open();
                outputStream.write(b);
                outputStream.flush();
                return;
            } catch (Exception e) {
                close();
                if (i == 1) {
                    throw e;
                }
            }
        }
    }

    public void write(int b) throws Exception {
        open();
        byte[] data = new byte[1];
        data[0] = (byte) b;
        write(data);
    }

    public void setBaudRate(int baudRate) throws Exception {
    }

    public void setTimeout(int timeout) throws Exception {

        this.readTimeout = timeout;

        if(socket != null)
            socket.setSoTimeout(readTimeout);
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) throws Exception {

    }

    public Object getSyncObject() throws Exception {
        return this;
    }

    public boolean isSearchByBaudRateEnabled() {
        return false;
    }
}
