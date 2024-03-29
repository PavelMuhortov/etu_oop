package com.example.market.swingview.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

/**
 * Панель инструментов
 */
public class TableToolbar
        extends JToolBar {

    public static final String TOTAL_ENTRIES = "Всего записей: ";
    /**
     * Кнопка "Добавить". Добавляет новую строку.
     */
    private JButton add;

    /**
     * Кнопка "Удалить". Удаляет выделенные строки
     */
    private JButton delete;

    /**
     * Кнопка "Сохранить". Сохраняет изменения
     */
    private JButton save;

    /**
     * Кнопка "Обновить". Обновляет данные
     */
    private JButton refresh;

    private JButton exportPdf;

    private JButton exportHtml;

    private JLabel totalEntries;

    public TableToolbar() {
        add = createButton("png/new.png", "Добавить");
        delete = createButton("png/delete.png", "Удалить");
        save = createButton("png/save-file.png", "Сохранить");
        refresh = createButton("png/refresh.png", "обновить");
        exportPdf = createButton("png/pdf.png", "Экспорт в PDF");
        exportHtml = createButton("png/html.png", "Экспорт в HTML");
        totalEntries = new JLabel(TOTAL_ENTRIES);
        add(add);
        add(delete);
        add(save);
        add(refresh);
        add(exportPdf);
        add(exportHtml);
        add(totalEntries);
    }

    private JButton createButton(String path, String text) {
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
            BufferedImage image = ImageIO.read(Objects.requireNonNull(resourceAsStream));
            image = resizeImg(image, 50, 50);
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText(text);
            button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            return button;
        } catch (Exception e) {
            return new JButton(text);
        }
    }

    private BufferedImage resizeImg(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }

    /**
     * Добавляет слушателя кнопки "Добавить"
     *
     * @param buttonListener
     * @return
     */
    public TableToolbar onAdd(Runnable buttonListener) {
        add.addActionListener(e -> buttonListener.run());
        return this;
    }

    /**
     * Добавляет сушателя кнопки "Удалить"
     *
     * @param buttonListener
     * @return
     */
    public TableToolbar onDelete(Runnable buttonListener) {
        delete.addActionListener(e -> buttonListener.run());
        return this;
    }

    /**
     * Добавляет сушателя кнопки "Сохранить"
     *
     * @param buttonListener
     * @return
     */
    public TableToolbar onSave(Runnable buttonListener) {
        save.addActionListener(e -> buttonListener.run());
        return this;
    }

    /**
     * Добавляет сушателя кнопки "Обновить"
     *
     * @param buttonListener
     * @return
     */
    public TableToolbar onRefresh(Runnable buttonListener) {
        refresh.addActionListener(e -> buttonListener.run());
        return this;
    }

    public TableToolbar onExportPdf(Runnable buttonListener) {
        exportPdf.addActionListener(e -> buttonListener.run());
        return this;
    }

    public TableToolbar onExportHtml(Runnable buttonListener) {
        exportHtml.addActionListener(e -> buttonListener.run());
        return this;
    }

    public void setTotalEntries(long value) {
        SwingUtilities.invokeLater(() -> {
            totalEntries.setText(TOTAL_ENTRIES + value);
        });
    }

}
