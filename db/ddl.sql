-- ---------------------
-- Enums
-- ---------------------
CREATE TYPE tipo_documento AS ENUM ('DNI', 'LE', 'LC', 'Pasaporte');


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
    codigoPostal VARCHAR(20),
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
    cuit VARCHAR(20) PRIMARY KEY,
    razon_social VARCHAR(100),
    telefono INTEGER,
    direccion_id INTEGER REFERENCES direccion(id) ON DELETE CASCADE
);

-- ---------------------
-- Tabla PersonaFisica
-- ---------------------
CREATE TABLE persona_fisica (
    cuit VARCHAR(20) PRIMARY KEY REFERENCES responsable_pago(cuit) ON DELETE CASCADE,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    tipo_documento tipo_documento,
    documento VARCHAR(20),
    fecha_nacimiento DATE
);

-- ---------------------
-- Tabla PersonaJuridica
-- ---------------------
CREATE TABLE persona_juridica (
    cuit VARCHAR(20) PRIMARY KEY REFERENCES responsable_pago(cuit) ON DELETE CASCADE
);

-- ---------------------
-- Tabla Huesped
-- ---------------------
CREATE TABLE huesped (
    cuit VARCHAR(20) PRIMARY KEY REFERENCES persona_fisica(cuit) ON DELETE CASCADE,
    posicion_frente_al_iva VARCHAR(50),
    telefono INTEGER,
    email VARCHAR(100),
    ocupacion VARCHAR(100)
);

-- ---------------------
-- Tabla Estadia
-- ---------------------
CREATE TABLE Estadia (
    idEstadia SERIAL PRIMARY KEY,
    horaCheckIn TIME,
    horaCheckOut TIME,
    fechaCheckIn DATE,
    fechaCheckOut DATE,
    idResponsablePago INT NOT NULL,
    FOREIGN KEY (idResponsablePago) REFERENCES ResponsablePago(idResponsablePago)
);

-- ---------------------
-- Tabla Servicio
-- ---------------------
CREATE TABLE Servicio (
    idServicio SERIAL PRIMARY KEY,
    tipo VARCHAR(50),
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
    estado VARCHAR(50),
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
    idReserva SERIAL PRIMARY KEY,
    estado VARCHAR(50),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    telefono VARCHAR(20),
    idHabitacion INT
);

-- ---------------------
-- Tabla Habitacion
-- ---------------------
CREATE TABLE Habitacion (
    numeroHabitacion SERIAL PRIMARY KEY,
    tipo VARCHAR(50),
    estado VARCHAR(50),
    numCamasSimples INT,
    numCamasDobles INT,
    capacidad INT,
    descuentoPorEstadiaLarga NUMERIC(5,2)
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
    estado VARCHAR(50),
    numero VARCHAR(50)
);
