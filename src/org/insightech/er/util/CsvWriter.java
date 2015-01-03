package org.insightech.er.util;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CSV�o�̓N���X
 * 
 * @author generator
 * @version $Id: CsvWriter.java,v 1.1 2008/08/17 10:49:17 h_nakajima Exp $
 */
public class CsvWriter {

    private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    private static final String DELIMITER = ",";

    private PrintWriter writer;

    private DateFormat dateFormat;

    private String delimiter;

    /**
     * �R���X�g���N�^
     * 
     * @param writer
     *            �o�͐�
     */
    public CsvWriter(PrintWriter writer) {
        this.writer = writer;
        this.delimiter = "";
        this.dateFormat = DEFAULT_FORMAT;
    }

    /**
     * Date �^�̃f�[�^���o�͂���ۂ̃t�H�[�}�b�g�`�����w�肵�܂�
     * 
     * @param format
     *            �t�H�[�}�b�g�`��
     */
    public void setDateFormat(String format) {
        this.dateFormat = new SimpleDateFormat(format);
    }

    /**
     * CSV�̂��߂ɕ�������G�X�P�[�v���܂��B
     * 
     * @param str
     *            �G�X�P�[�v�O�̕�����
     * @return �G�X�P�[�v���ꂽ������
     */
    public static String escape(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("\"", "\"\"");
    }

    /**
     * �I�u�W�F�N�g�̕�����\�����o�͂��܂�
     * 
     * @param object
     *            �I�u�W�F�N�g
     */
    public void print(Object object) {
        String value = null;

        if (object instanceof Date) {
            value = dateFormat.format((Date) object);
        } else {
            if (object == null) {
                value = "";
            } else {
                value = object.toString();
            }
        }

        writer.print(this.delimiter);

        writer.print("\"");
        writer.print(escape(value));
        writer.print("\"");

        this.setDelimiter();
    }

    /**
     * �f���~�^�[���o�͑ΏۂɃZ�b�g���܂�
     */
    private void setDelimiter() {
        this.delimiter = DELIMITER;
    }

    /**
     * �f���~�^�[���o�͑Ώۂ��烊�Z�b�g���܂�
     */
    private void resetDelimiter() {
        this.delimiter = "";
    }

    /**
     * ���s�R�[�h���o�͂��܂�
     */
    public void crln() {
        writer.print("\r\n");

        this.resetDelimiter();
    }

    /**
     * �o�͐����܂�
     */
    public void close() {
        this.writer.close();
    }

}
