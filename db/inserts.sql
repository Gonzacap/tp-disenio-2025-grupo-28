INSERT INTO pais (id, nombre)
VALUES
(1, 'España'),
(2, 'México'),
(3, 'Argentina');

INSERT INTO provincia (id, nombre, id_pais)
VALUES
(1, 'Santa Fe', 3),
(2, 'Córdoba', 3),
(3, 'Buenos Aires', 3);

-- ---------------------
-- Habitaciones
-- ---------------------
INSERT INTO Habitacion (tipo, estado, num_camas_simples, num_camas_dobles, capacidad, descuento_por_estadia_larga)
VALUES
    ('individualEstandar', 'disponible', 1, 0, 1, 0),
    ('dobleEstandar', 'disponible', 0, 1, 2, 10.00),
    ('suiteDoble', 'mantenimiento', 1, 1, 3, 15.50),
    ('dobleEstandar', 'ocupada', 0, 1, 2, 5.00);

-- ---------------------
-- Reservas
-- ---------------------
INSERT INTO Reserva (estado, nombre, apellido, telefono, fecha_desde, fecha_hasta)
VALUES
    ('generada', 'Juan', 'Pérez', '1111-1111', '2025-11-28', '2025-11-30'),
    ('confirmada', 'Ana', 'López', '2222-2222', '2025-12-01', '2025-12-05'),
    ('cancelada', 'Carlos', 'Gómez', '3333-3333', '2025-12-10', '2025-12-12');

INSERT INTO Reserva_Habitacion (id_reserva, numero_habitacion)
VALUES
    (1, 1),  -- Reserva 1 usa habitación 1
    (2, 2),  -- Reserva 2 usa habitación 2
    (2, 3),  -- Reserva 2 usa 2 habitaciones
    (3, 4);  -- Reserva 3 usa habitación 4
