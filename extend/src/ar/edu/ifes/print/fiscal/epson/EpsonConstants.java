package ar.edu.ifes.print.fiscal.epson;

public interface EpsonConstants {

	//El Controlador Fiscal como respuesta de cada comando recibido envia
	//un completo conjunto de indicadores de estados al Host, tanto para el 
	//estado del Impresor como del Controlador Fiscal. Es necesario analizar
	//ambas respuestas para saber si la orden enviada al Impresor Fiscal ha 
	//sido ejecutada en forma satisfactoria.
	
	//ESTADO FISCAL
	
	//Bit 0: Error de comprobacion de Memoria Fiscal. En el encendido, 
	//fracaso la verificacion de suma de la Memoria Fiscal. No funcionará 
	//el Impresor Fiscal.
	public int FST_FISCAL_MEMORY_CRC_ERROR = 0x0001;
	
	//Bit 1: Error de comprobacion de Memoria de Trabajo. En el encendido, 
	//fracaso la verificacion de suma de la Memoria de Trabajo. No funcionará 
	//el Impresor Fiscal.
	public int FST_WORKING_MEMORY_CRC_ERROR = 0x0002;
	
	//Bit 2: La bateria que alimenta la Memoria de Trabajo y el Reloj de 
	//Tiempo Real esta baja. Se debe llamar al Tecnico Fiscal antes de 
	//los 30 dias.
	
	
	//Bit 3: El comando recibido por la entrada serie del Host no es reconocido.
	public int FST_UNKNOWN_COMMAND = 0x0008;
	
	//Bit 4: Datos de campo no validos. Un campo de datos del comando 
	//recibido del Host tenia datos no validos (ej. datos no numericos en un campo numerico).
	public int FST_INVALID_DATA_FIELD = 0x0010;
	
	//Bit 5: Comando no valido para el estado fiscal. Se recibo un comando 
	//del Host que no es valido para el estado actual del Impresor Fiscal 
	//(ej. abrir un comprobante fiscal mientras hay abierto un comprobante no fiscal).
	public int FST_INVALID_COMMAND = 0x0020;
	
	//Bit 6: Se producira un desbordamiento de total de transaccion, diario o 
	//fiscal. Se emitio un comando que generara un desbordamiento de total. 
	//No se ejecuto el comando. El Host debe monitorear este bit y declarar 
	//un error en caso de que se produzca. Si junto con este el Bit 11 
	//estuviera tambien encendido, nos informa que se debe realizar un 
	//transporte de hoja en una Factura o Nota de Credito.
	public int FST_ACCUMULATOR_OVERFLOW = 0x0040;
	
	//Bit 7: Memoria Fiscal llena. No se puede abrir un comprobante fiscal 
	//cuando la Memoria Fiscal esta llena.
	public int FST_FISCAL_MEMORY_FULL = 0x0080;
	
	//Bit 8: Memoria Fiscal casi llena. La memoria fiscal esta dentro de 
	//los 40 cierres para llenarse. El Host debera emitir el mensaje de 
	//aviso apropiado
	public int FST_FISCAL_MEMORY_ALMOST_FULL = 0x0100;
	
	//Bit 9: Impresor fiscal certificado. Se activa este bit si se ha 
	//certificado el Impresor Fiscal. Si el Bit 10 esta en 0; el 
	//Impresor Fiscal esta en modo entrenamiento.
	public int FST_DEVICE_CERTIFIED = 0x0200;
	
	//Bit 10: Impresor Fiscal finalizado. Se activa este bit si se ha 
	//finalizado el Impresor Fiscal. Si el Bit 9 esta en 0; el 
	//Impresor Fiscal esta esta desfiscalizado por software.
	public int FST_DEVICE_FISCALIZED = 0x0400;
	
	//Bit 11: Se necesita que se haga un cierre de la Jornada Fiscal 
	//ya que han pasado 24 horas sin realizar un cierre 'Z' o se han 
	//enviado el numero maximo de items que acepta un Documento, por 
	//lo que se debe realizar el pago de la mercaderia registrada y 
	//continuar la facturacion en un Documento nuevo. Este indicador 
	//de estado es actualizado en el momento de iniciar un documento 
	//fiscal o cuando se consulta el estado y no hay un documento 
	//fiscal abierto.
	//Si este estuviera encendido junto con el bit 6 informa que en 
	//una Factura o Nota de Credito se necesita realizar un transporte 
	//de hoja debido a que el item que se desea facturar no entra en la Factura / Nota de Credito. 
	public int FST_DATE_ERROR = 0x0800;
	
	//Bit 12: Documento Fiscal abierto. Se activa este bit cada vez 
	//que hay un documento fiscal abierto (ej. Tique o Factura).
	public int FST_FISCAL_DOCUMENT_OPEN = 0x1000;
	
	//Bit 13: Documento Fiscal abierto o Documento No Fiscal abierto 
	//que se emite por el rollo de papel. Si el Bit 12 esta en 0
	//se tiene un Documento No Fiscal abierto y si el Bit 12 
	//esta en 1 se tiene un Documento Fiscal abierto.
	public int FST_DOCUMENT_OPEN = 0x2000;
	
	//Bit 14: Factura o Impresion en hoja suelta inicializada.
	//Factura u hoja suelta abierta. Se activa este bit cada vez 
	//que hay una factura u hoja suelta para imprimir.
	
	//Bit 15: OR logico de los bits 0 a 8 y 11. Se activa este bit
	//cada vez que hay alguno de los bits de 0 a 8 y 11 que indican error.
	public int FST_BITWISE_OR = 0x8000;
	
	//Estado de la Impresora
	
	//Bit 2: Error / falla de impresora. Significa que se ha cortado
	//el enlace entre el Controlador Fiscal y la Impresora Fiscal.
	public int PST_PRINTER_ERROR = 0x0004;
	
	//Bit 3: Impresora fuera de linea. La impresora no se comunico 
	//dentro de un periodo razonable de tiempo, o la impresora se 
	//ha quedado sen papel por mas de 5 seg.
	public int PST_PRINTER_OFFLINE = 0x0008;
	
	//Bit 5: Impresora con poco papel. Avisa que el papel para 
	//impresion de recibos esta proximo a agotarse, no obstante
	//se permite abrir nuevos comprobantes.
	public int PST_PAPER_LITTLE = 0x0020;
	
	//Bit 6: Bufer de impresora lleno. El controlador fiscal 
	//convierte los comandos para el impresor fiscal provenientes 
	//del Host y los coloca en un bufer antes de enviarlos
	//a la impresora EPSON.
	public int PST_PRINT_BUFFER_FULL = 0x0040;
	
	//Bit 7: Bufer de impresora vacio. Cuando el bufer de la 
	//Impresora Fiscal del Controlador Fiscal esta vacio, se 
	//activa este bit. Es una indicacion al Host de que todos
	//los datos fueron enviados a la Impresora Fiscal.
	public int PST_PRINT_BUFFER_EMPTY = 0x0080;
	
	//Bit 8: Estrada de hojas sueltas frontal preparada. Cuando 
	//la Impresora Fiscal habilito la toma de hojas sueltas, se
	//activa este bit.
	
	//Bit 9: Hoja suelta frontal preparada. Cuando la Impresora 
	//Fiscal tiene una hoja de papel suelto lista para ser impresa,
	//se activa este bit.
	
	//Bit 10: Toma de hojas para validacion preparada. Cuando la 
	//Impresora Fiscal indica que esta lista para recibir la hoja
	//a validar, se activa este bit.
	
	//Bit 11: Papel para validacion presente. Cuando la Impresora
	//Fiscal esta lista para imprimir porque se ingreso el papel
	//a ser validado, se activa este bit.
	
	//Bit 12: Cajon de dinero abierto. Se activa este bit cuando 
	//se abre alguno de los cajones de efectivo.
	public int PST_MONEY_DRAWER_OPEN = 0x1000;
	
	//Bit 14: Impresora sin papel. Si no existe papel a ser 
	//impreso se activa este bit. Si este bit esta activado
	//no se permite continuaar la emision de documentos hasta
	//que se restablezca el suministro de papel a la impresora.
	public int PST_PAPER_OUT = 0x4000;
	
	//Bit 15: OR logico entre los bits 0 a 6 y 14. Se activa este 
	//bit toda vez que esten activos los bits 0 a 6 y 14. Tambien
	//se activa si se emite un comando de impresion en hojas
	//sueltas y no se ingreso el papel a ser impreso.
	public int PST_BITWISE_OR = 0x8000;
	

}
