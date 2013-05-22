package ar.edu.ifes.print.fiscal.epson;

public interface EpsonCommands {
	
	//Solicitud de estado (hoja 12)
		public int CMD_STATUS_REQUEST = 0x2a;
		
		//Bloqueo del controlador fiscal (hoja 18)
		public int CMD_LOCK_FISCAL = 0x36;
		
		//Cierre diario (hoja 19)
		public int CMD_DAILY_CLOSE = 0x39;
		
		//Reporte de memoria fiscal por fecha (hoja 21)
		public int CMD_DAILY_CLOSE_BY_DATE = 0x3a;
		
		//Reporte de memoria fiscal por numero de cierre z (hoja 22)
		public int CMD_DAILY_CLOSE_BY_NUMBER = 0x3b;
		
		//Abrir comprobante tique fiscal (hoja 23)
		public int CMD_OPEN_FISCAL_RECEIPT = 0x40;
		
		//Imprimir texto fiscal en tique fiscal (hoja 24)
		public int CMD_PRINT_FISCAL_TEXT = 0x41;
		
		//Imprimir item de linea en tique fiscal (hoja 25)
		public int CMD_PRINT_LINE_ITEM = 0x42;
		
		//Subtotal de tique fiscal (hoja 26)
		public int CMD_SUBTOTAL = 0x43;
		
		//Pagos / Descuentos / Recargos / Cancelar en Tique (hoja 27)
		
		//Cerrar comprobante fiscal tique (hoja 29)
		public int CMD_CLOSE_FISCAL_RECEIPT = 0x45;
		
		//Abrir documento no fiscal (hoja 30)
		public int CMD_OPEN_NON_FISCAL_RECEIPT = 0x48;
		
		//Imprimir texto en documento no fiscal (hoja 30)
		public int CMD_PRINT_NON_FISCAL_TEXT = 0x49;
		
		//Cerrar documento no fiscal (hoja 31)
		public int CMD_CLOSE_NON_FISCAL_RECEIPT = 0x4a;
		
		//En el libro en esta parte van los documentos no fiscales homologados
		
		//Seleccionar preferencias de usuario (hoja 37)
		//Este comando permite realizar configuraciones sobre la impresion en Hoja Suelta y/o Formulario Continuo, asi como tambien 
		//seleccionar determinadas preferencias en comprobantes. Para saber cual es la configuracion actual, se puede utilizar el comando
		//Leer Preferencias de Usuario
		public int CMD_SELECT_USER_PREFERENCE = 0x5a;
		
		//Leer preferencias del usuario (hoja 38)
		public int CMD_GET_USER_PREFERENCE = 0x5b;
		
		//Preparar estacion principal (hoja 40)
		public int CMD_PREPARE_HOME_STATION = 0x5c;
		
		//Cortar Papel (hoja 41)
		public int CMD_CUT = 0x4b;
		
		//Avanzar comprobante o tique / cinta de auditoria / ambos (hoja 41)
		//avanzar papel en comprobante o tique
		public int CMD_FEED_TICKET = 0x50;
		
		//avanzar papel en cinta testigo (auditoria)
		public int CMD_FEED_JOURNAL = 0x51;
		
		//avanzar papel en ambos a la vez
		public int CMD_FEED_TICKET_AND_JOURNAL = 0x52;
		
		//Avanzar hoja suelta (hoja 42)
		//Este comando hace avanzar una hoja suelta segun la cantidad de lineas especificada.
		//Se utiliza unicamente para Estacion Slip.
		public int CMD_FEET_LOOSE_LEAF = 0x53;
		
		//Establecer fecha y hora (hoja 43)
		public int CMD_SET_DATE_TIME = 0x58;
		
		//Obtener fecha y hora (hoja 43)
		public int CMD_GET_DATE_TIME = 0x59;
		
		

}
