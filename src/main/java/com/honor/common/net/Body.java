package com.honor.common.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public abstract class Body {
    public static final String JSON = "application/json; charset=utf-8";
    public static final String FORM = "application/x-www-form-urlencoded";
    public static final String FILE = "multipart/form-data;boundary=******";

    protected byte[] data;

    public abstract long getContentLength();

    public abstract String getContentType();

    public abstract byte[] data();

    public abstract String string();

    public abstract String string(String charset);

    public abstract InputStream getInputStream();

    public static Body create(final String mediaType, final String content) {
        try {
            return create(mediaType, content.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return create(mediaType, content.getBytes());
    }

    public static Body create(final String mediaType, final byte[] content) {
        return create(mediaType, content, 0, content.length);
    }

    public static Body create(final String mediaType, final File file) {
        try {
            return create(mediaType, new FileInputStream(file), file.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Body create(final String mediaType, final InputStream inputStream, final long count) {
        return new Body() {
            long contentLength;

            @Override
            public long getContentLength() {
                if (data == null)
                    data();
                return contentLength;
            }

            @Override
            public String getContentType() {
                return mediaType;
            }

            @Override
            public byte[] data() {
                if (this.data != null)
                    return this.data;
                int capacity = (int) count;
                if (count < 0) {
                    capacity = 1024;
                }
                this.data = new byte[capacity];
                try {
                    int offset = 0;
                    int length;
                    while ((length = inputStream.read(this.data, offset, capacity - offset)) != -1) {
                        offset += length;
                        if (offset == capacity) {
                            capacity = capacity << 1;
                            data = Arrays.copyOf(data, capacity);
                        }
                    }
                    contentLength = offset;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return this.data;
            }

            @Override
            public String string() {
                return new String(data(), 0, (int) getContentLength());
            }

            @Override
            public String string(String charset) {
                try {
                    return new String(data(), 0, (int) getContentLength(), charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return string();
            }


            @Override
            public InputStream getInputStream() {
                return inputStream;
            }
        };
    }

    public static Body create(final String mediaType, final byte[] content, final int offset, final int length) {
        return new Body() {
            @Override
            public long getContentLength() {
                return length;
            }

            @Override
            public String getContentType() {
                return mediaType;
            }

            @Override
            public byte[] data() {
                if (content == null) {
                    throw new RuntimeException("Body.create error:content==null");
                }
                if (offset < 0 || length < 0) {
                    throw new RuntimeException("Body.create error:offset<0||length<0");
                }
                if (offset + length > content.length) {
                    throw new RuntimeException("Body.create error:offset+length>content.length");
                }
                if (offset == 0 && length == content.length) {
                    return content;
                } else {
                    byte[] temp = new byte[length];
                    System.arraycopy(content, offset, temp, 0, length);
                    return temp;
                }

            }

            @Override
            public String string() {


                return new String(content, offset, length);
            }

            @Override
            public String string(String charset) {
                try {
                    return new String(content, offset, length, charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return string();
            }

            @Override
            public InputStream getInputStream() {
                return null;
            }
        };
    }


}
