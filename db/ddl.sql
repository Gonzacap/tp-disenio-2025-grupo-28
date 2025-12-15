-- ---------------------
-- Enums
-- ---------------------
CREATE TYPE tipo_documento AS ENUM ('DNI', 'LE', 'LC', 'Pasaporte');
CREATE TYPE TipoEstadia AS ENUM ('COMPLETA', 'MEDIA_DIARIA', 'EXTENDIDA');
CREATE TYPE EstadoCheque AS ENUM ('enCartera', 'cobrado', 'rechazado');
CREATE TYPE EstadoEstadia AS ENUM ('reservada', 'enCurso', 'extendida', 'finalizada', 'cancelada');
CREATE TYPE EstadoHabitacion AS ENUM ('disponible', 'ocupada', 'reservada', 'mantenimiento');
CREATE TYPE EstadoReserva AS ENUM ('generada', 'confirmada', 'cumplida', 'cancelada');
CREATE TYPE TipoHabitacion AS ENUM ('dobleSuperior', 'superiorFamilyPlan', 'suiteDoble', 'individualEstandar', 'dobleEstandar');
CREATE TYPE TipoServicio AS ENUM ('disponible', 'ocupada', 'reservada', 'mantenimiento');
CREATE TYPE EstadoFactura AS ENUM ('pendiente', 'pagada', 'anuladaConNota', 'noCobrable', 'borrador');

-- ---------------------
-- Tabla Pais
-- ---------------------
CREATE TABLE Pais (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- ---------------------
-- Tabla Provincia
-- ---------------------
CREATE TABLE provincia (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_pais INT NOT NULL,
    FOREIGN KEY (id_pais) REFERENCES Pais(id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla Localidad
-- ---------------------
CREATE TABLE localidad (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(20),
    id_provincia INT NOT NULL,
    FOREIGN KEY (id_provincia) REFERENCES Provincia(id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla Direccion
-- ---------------------
CREATE TABLE direccion (
    id SERIAL PRIMARY KEY,
    direccion VARCHAR(50),
    numero INT,
    depto VARCHAR(10),
    piso VARCHAR(10),
    nacionalidad VARCHAR(50),
    id_localidad INT NOT NULL,
    FOREIGN KEY (id_localidad) REFERENCES Localidad(id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla ResponsablePago
-- ---------------------
CREATE TABLE responsable_pago (
    id SERIAL NOT NULL,
    cuit VARCHAR(20) NULL,
    razon_social VARCHAR(100),
    telefono VARCHAR(20),
    direccion_id INTEGER REFERENCES direccion(id) ON DELETE CASCADE,
    CONSTRAINT responsable_pago_pkey PRIMARY KEY (id),
    CONSTRAINT responsable_pago_cuit_unique UNIQUE (cuit),
    CONSTRAINT responsable_pago_direccion_fk FOREIGN KEY (direccion_id) references direccion (id)
);

-- ---------------------
-- Tabla PersonaFisica
-- ---------------------
CREATE TABLE persona_fisica (
    id INTEGER NOT NULL,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    tipo_documento tipo_documento,
    documento VARCHAR(20),
    fecha_nacimiento DATE NULL,
    CONSTRAINT persona_fisica_pkey PRIMARY KEY (id),
    CONSTRAINT uk_persona_doc UNIQUE (tipo_documento, documento);
    CONSTRAINT persona_fisica_id_fk FOREIGN KEY (id) REFERENCES responsable_pago (id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla PersonaJuridica
-- ---------------------
CREATE TABLE persona_juridica (
    id INTEGER NOT NULL,
    CONSTRAINT persona_juridica_pkey PRIMARY KEY (id),
    CONSTRAINT persona_juridica_id_unique UNIQUE (id),
    CONSTRAINT persona_juridica_id_fk FOREIGN KEY (id) REFERENCES responsable_pago (id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla Huesped
-- ---------------------
CREATE TABLE huesped (
    id INTEGER NOT NULL,
    posicion_frente_al_iva VARCHAR(50),
    email VARCHAR(100),
    ocupacion VARCHAR(100),
    CONSTRAINT huesped_pkey PRIMARY KEY (id),
    CONSTRAINT huesped_id_fk FOREIGN KEY (id) REFERENCES persona_fisica (id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla Estadia
-- ---------------------
CREATE TABLE Estadia (
    id_estadia SERIAL NOT NULL,
    hora_check_in TIME,
    hora_check_out TIME,
    fecha_check_in DATE,
    fecha_check_out DATE,
    estado_estadia EstadoEstadia,
    id_reserva INT NULL,
    fecha_ingreso DATE NULL,
    -- huesped character varying(255) null,
    habitacion_id INT NULL,
    responsable_id INT NULL
    CONSTRAINT estadia_pkey PRIMARY KEY (id_estadia),
    CONSTRAINT fk_estadia_responsable_id FOREIGN KEY (responsable_id) REFERENCES responsable_pago (id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla Servicio
-- ---------------------
CREATE TABLE Servicio (
    idServicio SERIAL PRIMARY KEY,
    tipo TipoServicio,
    costo NUMERIC(10,2)
);

-- Tabla intermedia Estadia_Servicio
CREATE TABLE Estadia_Servicio (
    idEstadia INT NOT NULL,
    idServicio INT NOT NULL,
    PRIMARY KEY (idEstadia, idServicio),
    FOREIGN KEY (idEstadia) REFERENCES Estadia(idEstadia),
    FOREIGN KEY (idServicio) REFERENCES Servicio(idServicio)
);

-- ---------------------
-- Tabla Factura
-- ---------------------
CREATE TABLE Factura (
    idFactura SERIAL PRIMARY KEY,
    fechaEmision DATE,
    estado EstadoFactura,
    idEstadia INT NOT NULL,
    idTipoPago INT,
    FOREIGN KEY (idEstadia) REFERENCES Estadia(idEstadia)
);

-- ---------------------
-- Tabla TipoPago
-- ---------------------
CREATE TABLE TipoPago (
    idTipoPago SERIAL PRIMARY KEY,
    descripcion VARCHAR(100)
);

-- ---------------------
-- Tabla ItemFactura
-- ---------------------
CREATE TABLE ItemFactura (
    idItemFactura SERIAL PRIMARY KEY,
    descripcion VARCHAR(100),
    cantidad INT,
    monto NUMERIC(10,2),
    costoUnitario NUMERIC(10,2),
    idFactura INT NOT NULL,
    FOREIGN KEY (idFactura) REFERENCES Factura(idFactura)
);

-- ---------------------
-- Tabla NotaCredito
-- ---------------------
CREATE TABLE NotaCredito (
    idNotaCredito SERIAL PRIMARY KEY,
    numero VARCHAR(50),
    fecha DATE,
    importeTotal NUMERIC(10,2),
    importeIVA NUMERIC(10,2),
    importeNeto NUMERIC(10,2),
    idResponsablePago INT,
    FOREIGN KEY (idResponsablePago) REFERENCES ResponsablePago(idResponsablePago)
);

-- ---------------------
-- Tabla Reserva
-- ---------------------
CREATE TABLE Reserva (
    id_reserva SERIAL PRIMARY KEY,
    estado EstadoReserva NOT NULL,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    telefono VARCHAR(20),
    fecha_desde DATE NOT NULL,
    fecha_hasta DATE NOT NULL,
    huesped_id INTEGER NULL,
    CONSTRAINT fk_huesped_huesped_id FOREIGN KEY (huesped_id) REFERENCES huesped (id) ON DELETE SET NULL
);

-- ---------------------
-- Tabla Habitacion
-- ---------------------
CREATE TABLE Habitacion (
    numero_habitacion SERIAL PRIMARY KEY,
    tipo TipoHabitacion NOT NULL,
    estado EstadoHabitacion NOT NULL,
    num_camas_simples INT,
    num_camas_dobles INT,
    capacidad INT,
    descuento_por_estadia_larga NUMERIC(10,2)
);

-- ---------------------
-- Tabla Reserva_Habitacion
-- ---------------------
CREATE TABLE Reserva_Habitacion (
    id_reserva INT NOT NULL,
    numero_habitacion INT NOT NULL,
    CONSTRAINT pk_reserva_habitacion PRIMARY KEY (id_reserva, numero_habitacion),
    CONSTRAINT fk_rh_reserva FOREIGN KEY (id_reserva)
        REFERENCES Reserva(id_reserva)
        ON DELETE CASCADE,
    CONSTRAINT fk_rh_habitacion FOREIGN KEY (numero_habitacion)
        REFERENCES Habitacion(numero_habitacion)
        ON DELETE RESTRICT
);

-- ---------------------
-- Tabla Reserva_Acompanantes
-- ---------------------
CREATE TABLE Reserva_Acompanantes (
    id_reserva INT NOT NULL,
    id_persona INT NOT NULL,
    CONSTRAINT pk_reserva_acompanantes PRIMARY KEY (id_reserva, id_persona),
    CONSTRAINT fk_reserva_acompanantes_reserva FOREIGN KEY (id_reserva) REFERENCES reserva (id_reserva) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_acompanantes_persona FOREIGN KEY (id_persona) REFERENCES persona_fisica (id) ON DELETE CASCADE
);


-- ---------------------
-- Tabla MonedaExtranjera
-- ---------------------
CREATE TABLE MonedaExtranjera (
    idMoneda SERIAL PRIMARY KEY,
    tipoMoneda VARCHAR(50),
    montoOriginal NUMERIC(10,2),
    cotizacionDia NUMERIC(10,2)
);

-- ---------------------
-- Tabla Tarjeta
-- ---------------------
CREATE TABLE Tarjeta (
    idTarjeta SERIAL PRIMARY KEY,
    tarjetaNumerica VARCHAR(20),
    fechaVenc DATE,
    montoOriginal NUMERIC(10,2)
);

-- ---------------------
-- Tabla TarjetaDebito
-- ---------------------
CREATE TABLE TarjetaDebito (
    idTarjetaDebito SERIAL PRIMARY KEY,
    idTarjeta INT,
    FOREIGN KEY (idTarjeta) REFERENCES Tarjeta(idTarjeta)
);

-- ---------------------
-- Tabla TarjetaCredito
-- ---------------------
CREATE TABLE TarjetaCredito (
    idTarjetaCredito SERIAL PRIMARY KEY,
    idTarjeta INT,
    FOREIGN KEY (idTarjeta) REFERENCES Tarjeta(idTarjeta)
);

-- ---------------------
-- Tabla TransferenciaBancaria
-- ---------------------
CREATE TABLE TransferenciaBancaria (
    idTransferencia SERIAL PRIMARY KEY,
    cbu VARCHAR(50),
    alias VARCHAR(50)
);

-- ---------------------
-- Tabla Efectivo
-- ---------------------
CREATE TABLE Efectivo (
    idEfectivo SERIAL PRIMARY KEY,
    plaza VARCHAR(50),
    monto NUMERIC(10,2),
    banco VARCHAR(50)
);

-- ---------------------
-- Tabla Cheque
-- ---------------------
CREATE TABLE Cheque (
    idCheque SERIAL PRIMARY KEY,
    fechaEmision DATE,
    fechaPago DATE,
    aceptado BOOLEAN,
    estado EstadoCheque,
    numero VARCHAR(50)
);
CREATE TABLE EstadoHabitacionPeriodo (
    idEstadoHabitacionPeriodo SERIAL PRIMARY KEY,
    estado EstadoHabitacion NOT NULL,
    fechaDesde DATE NOT NULL,
    fechaHasta DATE NOT NULL,
    numeroHabitacion INT NOT NULL,
    FOREIGN KEY (numeroHabitacion) REFERENCES Habitacion(numeroHabitacion)
);
