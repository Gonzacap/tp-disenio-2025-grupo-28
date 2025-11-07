-- ---------------------
-- Tabla Pais
-- ---------------------
CREATE TABLE Pais (
    idPais SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- ---------------------
-- Tabla Provincia
-- ---------------------
CREATE TABLE Provincia (
    idProvincia SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    idPais INT NOT NULL,
    FOREIGN KEY (idPais) REFERENCES Pais(idPais)
);

-- ---------------------
-- Tabla Localidad
-- ---------------------
CREATE TABLE Localidad (
    idLocalidad SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigoPostal VARCHAR(20),
    idProvincia INT NOT NULL,
    FOREIGN KEY (idProvincia) REFERENCES Provincia(idProvincia)
);

-- ---------------------
-- Tabla Direccion
-- ---------------------
CREATE TABLE Direccion (
    idDireccion SERIAL PRIMARY KEY,
    numero VARCHAR(10),
    depto VARCHAR(10),
    piso VARCHAR(10),
    direccion TEXT,
    nacionalidad VARCHAR(50),
    idLocalidad INT NOT NULL,
    FOREIGN KEY (idLocalidad) REFERENCES Localidad(idLocalidad)
);

-- ---------------------
-- Tabla ResponsablePago
-- ---------------------
CREATE TABLE ResponsablePago (
    idResponsablePago SERIAL PRIMARY KEY,
    cuit VARCHAR(20),
    direccion TEXT,
    telefono VARCHAR(20),
    razonSocial VARCHAR(100)
);

-- ---------------------
-- Tabla PersonaFisica
-- ---------------------
CREATE TABLE PersonaFisica (
    idPersonaFisica SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    tipoDocumento VARCHAR(50),
    documento VARCHAR(50),
    idResponsablePago INT NOT NULL,
    FOREIGN KEY (idResponsablePago) REFERENCES ResponsablePago(idResponsablePago)
);

-- ---------------------
-- Tabla PersonaJuridica
-- ---------------------
CREATE TABLE PersonaJuridica (
    idPersonaJuridica SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    idResponsablePago INT NOT NULL,
    FOREIGN KEY (idResponsablePago) REFERENCES ResponsablePago(idResponsablePago)
);

-- ---------------------
-- Tabla Huesped
-- ---------------------
CREATE TABLE Huesped (
    idHuesped SERIAL PRIMARY KEY,
    email VARCHAR(100),
    ocupacion VARCHAR(50),
    fechaNacimiento DATE,
    posicionFrenteAlIva VARCHAR(50),
    idPersonaFisica INT,
    idPersonaJuridica INT,
    FOREIGN KEY (idPersonaFisica) REFERENCES PersonaFisica(idPersonaFisica),
    FOREIGN KEY (idPersonaJuridica) REFERENCES PersonaJuridica(idPersonaJuridica)
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
