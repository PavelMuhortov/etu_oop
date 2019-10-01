package com.example.market.swingview.view;

import com.example.market.core.model.Model;
import com.example.market.core.view.TableView;
import com.example.market.core.viewmodel.TableViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Отображение данных в табличном виде
 *
 * @param <M>
 */
public class SwingTableView<M extends Model<M>>
        extends JPanel
        implements TableView<M> {

    private static final Logger LOG = LoggerFactory.getLogger(SwingTableView.class);

    private List<String> propertyNames;

    private TableViewModel<M> viewModel;

    private DefaultTableModel tableModel;

    private JTable table;

    /**
     * Инициализирует компоненты отображения
     */
    @SuppressWarnings("deprecation")
    @Override
    public void init() {
        M model = viewModel.newOne();
        propertyNames = model.getPropertyNames();
        setLayout(new BorderLayout());
        TableToolbar toolBar = new TableToolbar();
        add(toolBar, BorderLayout.NORTH);
        List<String> propertyDisplayNames = propertyNames.stream()
                .map(model::getDisplayName)
                .collect(Collectors.toList());
        SearchPanel searchPanel = new SearchPanel(propertyNames, propertyDisplayNames);
        setSearchPanelListeners(searchPanel);
        add(searchPanel, BorderLayout.SOUTH);
        String[] propertyDispNamesArray = Stream.concat(Stream.of("Id"), propertyDisplayNames.stream())
                .toArray(String[]::new);
        tableModel = new DefaultTableModel(propertyDispNamesArray, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0));
        setToolbarListeners(toolBar);
        Box contents = new Box(BoxLayout.Y_AXIS);
        add(contents, BorderLayout.CENTER);
        contents.add(new JScrollPane(table));
        fillTable();
    }

    /**
     * Устанавливает слушателей панели поиска
     *
     * @param searchPanel
     */
    private void setSearchPanelListeners(SearchPanel searchPanel) {
        searchPanel.onFilter(filterOptions -> fillTable(readData(filterOptions)));
    }

    private Collection<M> readData(SearchPanel.FilterOptions filterOptions) {
        try {
            if (filterOptions != null) {
                return viewModel.findBy(filterOptions.getProperty(), filterOptions.getValue());
            } else {
                return viewModel.getAll();
            }
        } catch (Exception e) {
            showAlert("Невозможно загрузить данные", e);
            return Collections.emptyList();
        }
    }

    /**
     * Выводит сообщение об ошибке
     *
     * @param message сообщение
     * @param e       исклчение
     */
    private void showAlert(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getLocalizedMessage());
    }

    /**
     * Устанавливает слушателей панели инструментов
     *
     * @param toolBar
     */
    private void setToolbarListeners(TableToolbar toolBar) {
        toolBar.onDelete(this::deleteElements)
                .onAdd(this::addElement)
                .onSave(this::saveData)
                .onRefresh(this::fillTable);
    }

    private void saveData() {
        save();
        fillTable();
    }

    private void addElement() {
        LOG.debug("Add entry event received");
        final M model = viewModel.newOne();
        addNewRow(model);
    }

    private void deleteElements() {
        final int[] rows = table.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            long modelId = (long) tableModel.getValueAt(rows[i], 0);
            try {
                LOG.debug("Remove element Id: {}", modelId);
                viewModel.delete(modelId);
            } catch (Exception ignore) {
            }
            tableModel.removeRow(rows[i] - i);

        }
    }

    /**
     * Заполняет отображение данными
     */
    private void fillTable() {
        fillTable(readData(null));
    }

    @Override
    public void setViewModel(TableViewModel<M> viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * сохраняет измененные данные
     */
    private void save() {
        try {
            int rows = table.getModel().getRowCount();
            for (int i = 0; i < rows; i++) {
                long id = (long) tableModel.getValueAt(i, 0);
                M model = viewModel.newOne();
                model.setId(id);
                for (int j = 0; j < propertyNames.size(); j++) {
                    String propertyName = propertyNames.get(j);
                    String valueAt = (String) tableModel.getValueAt(i, j + 1);
                    model.setPropertyValue(propertyName, valueAt);
                }
                LOG.debug("Saving data {}", model.toString());
                viewModel.save(model);
            }
        } catch (Exception e) {
            showAlert("Невозможно сохранить данные", e);
        }
    }

    /**
     * заполняет отображение данными
     *
     * @param data
     */
    private void fillTable(Collection<M> data) {
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        data.forEach(this::addNewRow);
    }

    /**
     * добавляет новую строку в таблицу
     *
     * @param model
     */
    private void addNewRow(M model) {
        final Object[] values = Stream.concat(Stream.of(model.getId()), propertyNames.stream()
                .map(model::getPropertyValue))
                .toArray();
        tableModel.addRow(values);
    }

}
