-- Очищаем таблицу перед вставкой тестовых данных
DELETE FROM department;

-- Вставляем тестовые данные
INSERT INTO department (id_address, name) VALUES
(1, 'IT Department'),
(2, 'HR Department'),
(3, 'Finance Department'),
(4, 'Marketing Department'),
(5, 'Sales Department'); 