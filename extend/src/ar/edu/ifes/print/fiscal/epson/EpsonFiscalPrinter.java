package ar.edu.ifes.print.fiscal.epson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import ar.com.ergio.print.fiscal.BasicFiscalPrinter;
import ar.com.ergio.print.fiscal.FiscalPacket;
import ar.com.ergio.print.fiscal.comm.FiscalComm;
import ar.com.ergio.print.fiscal.exception.FiscalPrinterIOException;
import ar.com.ergio.print.fiscal.exception.FiscalPrinterStatusError;
import ar.com.ergio.print.fiscal.hasar.HasarFiscalPacket;
import ar.com.ergio.print.fiscal.msg.FiscalMessage;
import ar.com.ergio.print.fiscal.msg.FiscalMessages;
import ar.com.ergio.print.fiscal.msg.MsgRepository;

/**
 * Impresora Fiscal Epson. Funcionalidad comun a todos los modelos de Epson.
 * Implementa la interfaz <code>EpsonCommands</code>. Cualquier modelo nuevo
 * de Epson a implementar debería ser una especialización de esta clase,
 * permitiendo sobreescribir algunos o todos los comandos implementados
 * por defecto en esta clase.
 * @author Bosso Armando
 * @date 04/05/2013
 */

public abstract class EpsonFiscalPrinter extends BasicFiscalPrinter implements EpsonCommands,EpsonConstants

{
	
	// Tipo de comprobante fiscal
	public static final int FACTURA = 1;
	public static final int RECIBO = 2;
	public static final int NOTA_DEBITO = 3;
	
	// Responsabilidad frente al IVA
	/** Responsabilidad frente al IVA: Responsable inscripto */
	protected static final String RESPONSABLE_INSCRIPTO = "I";
	/** Responsabilidad frente al IVA: Responsable no inscripto */
	protected static final String RESPONSABLE_NO_INSCRIPTO = "K";
	/** Responsabilidad frente al IVA: Exento */
	protected static final String EXENTO = "E";
	/** Responsabilidad frente al IVA: No responsable */
	protected static final String NO_RESPONSABLE = "N";
	/** Responsabilidad frente al IVA: Consumidor final */
	protected static final String CONSUMIDOR_FINAL = "F";
	/** Responsabilidad frente al IVA: Responsable no inscripto, venta de bienes de uso */
	protected static final String RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO = "B";
	/** Responsabilidad frente al IVA: Responsable monotributo */
	protected static final String RESPONSABLE_MONOTRIBUTO = "M";
	/** Responsabilidad frente al IVA: Monotributista social */
	protected static final String MONOTRIBUTISTA_SOCIAL = "T";
	/** Responsabilidad frente al IVA: PequeÃ±o contribuyente eventual */
	protected static final String PEQUENO_CONTRIBUYENTE_EVENTUAL = "C";
	/** Responsabilidad frente al IVA: PequeÃ±o contribuyente eventual social */
	protected static final String PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL = "V";
	/** Responsabilidad frente al IVA: No categorizado */
	protected static final String NO_CATEGORIZADO = "U";

	
	// Tipo de documento
	/** C.U.I.T. */
	protected static final String CUIT = "T";
	/** C.U.I.L. */
	protected static final String CUIL = "L";
	/** Libreta de enrolamiento */
	protected static final String LIBRETA_DE_ENROLAMIENTO = "E";
	/** Libreta cÃ­vica */
	protected static final String LIBRETA_CIVICA = "V";
	/** Documento nacional de identidad */
	protected static final String DNI = "D";
	/** Pasaporte */
	protected static final String PASAPORTE = "P";
	/** CÃ©dula de identidad */
	protected static final String CEDULA = "C";
	/** Sin calificador */
	protected static final String SIN_CALIFICADOR = " ";
	
	// Formato de código de barras
	/** Código de barras EAN 13 */
	protected static final Integer EAN_13 = 2;
	/** Código de barras EAN 8 */
	protected static final Integer EAN_8 = 3;
	/** Código de barras UPCA */
	protected static final Integer UPCA  = 0;
	/** Código de barras ITF 2 de 5 */
	protected static final Integer ITF = 5;
	
	
	// Opciones de operación del comando ReturnRecharge
	/** ReturnRecharge: Operación Devolución de Envases */
	protected static final String CONTAINER_RETURN = "e";
	/** ReturnRecharge: Operación Descuento / Recargo */
	protected static final String DISCOUNT_RECHARGE = "B";
	
	
	/** Conjunto de caracteres para realizar la conversión a string de los paquetes fiscales */
	private String encoding = "ISO8859_1";	// ISO 8859-1, Latin alphabet No. 1.
	/** Año base para validación de fechas */
	private int baseRolloverYear = 1997;
	/** Estado actual de la impresora */
	private int printerStatus;
	/** Estado actual del controlador fiscal */
	private int fiscalStatus;
	/** Posibles mensajes de estado de la impresora */
	private Map<Integer,FiscalMessage> printerStatusMsgs;
	/** Posibles mensajes de estado del controlador fiscal */
	private Map<Integer,FiscalMessage> fiscalStatusMsgs;
	/** Códigos de mensajes de estado de la impresora */
	private int[] printerStatusCodes = { PST_PRINTER_BUSY, PST_PRINTER_ERROR, PST_PRINTER_OFFLINE,
										 PST_JOURNAL_PAPER_OUT, PST_TICKET_PAPER_OUT, PST_PRINT_BUFFER_FULL,
										 PST_PRINT_BUFFER_EMPTY, PST_PRINTER_COVER_OPEN, PST_MONEY_DRAWER_CLOSED
										};
	
	
	/** Mapeo entre categorias de IVA de las clases de documentos y los valores
	 * esperados por las impresoras fiscales de esta marca. */
	private Map<Integer, String> ivaResponsabilities;
	/** Mapeo entre los tipos de identificación de clientes de las clases
	 * de documentos y los valores esperados por las impresoras de esta marca. */
	private Map<Integer, String> identificationTypes;
	/** Mapeo entre los tipos de documentos de las clases de documentos y
	 * los valores esperados por las impresoras de esta marca. */
	private Map<String, String> documentTypes;
	
	public EpsonFiscalPrinter() {
		super();
	}
	
	/**
	 * @param fiscalComm
	 */
	
	public EpsonFiscalPrinter(FiscalComm fiscalComm) {
		super(fiscalComm);
	}
	
	public FiscalPacket cmdBarCode(Integer codeType, String data, boolean printNumbers) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_BAR_CODE);
		int i = 1;
		cmd.setNumber(i++, codeType, false);
		cmd.setText(i++, data, false);
		cmd.setBoolean(i++, printNumbers, "N", "x", false);
		cmd.setText(i++, "x", false);
		return cmd;
	}
	
	public FiscalPacket cmdCancelDocument() {
		FiscalPacket cmd = createFiscalPacket(CMD_CANCEL_DOCUMENT);
		return cmd;
	}

	public FiscalPacket cmdChangeIVAResponsibility(String ivaResponsability) {
		FiscalPacket cmd = createFiscalPacket(CMD_CHANGE_IVA_RESPONSIBILITY);
		int i = 1;
		cmd.setText(i++, ivaResponsability, false);
		return cmd;
	}

	public FiscalPacket cmdCloseDNFH(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_DNFH);
		int i = 1;
		cmd.setNumber(i++, copies, true);
		return cmd;
	}

	public FiscalPacket cmdCloseFiscalReceipt(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies, true);
		return cmd;
	}

	public FiscalPacket cmdCloseNonFiscalReceipt(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_NON_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies, true);
		return cmd;
	}

	public FiscalPacket cmdDailyClose(String docType) {
		FiscalPacket cmd = createFiscalPacket(CMD_DAILY_CLOSE);
		int i = 1;
		cmd.setText(i++, docType, false);
		return cmd;
	}

	public FiscalPacket cmdDoubleWidth() {
		FiscalPacket cmd = createFiscalPacket(CMD_DOUBLE_WIDTH);
		return cmd;
	}	
	public FiscalPacket cmdSetGeneralConfiguration(boolean printConfigReport, boolean loadDefaultData, BigDecimal finalConsumerLimit, BigDecimal ticketInvoiceLimit, BigDecimal ivaNonInscript, Integer copies, Boolean printChange, Boolean printLabels, String ticketCutType, Boolean printFramework, Boolean reprintDocuments, String balanceText, Boolean paperSound, String paperSize) {
			FiscalPacket cmd = createFiscalPacket(CMD_SET_GENERAL_CONFIGURATION);
			int i = 1;
			cmd.setBoolean(i++, printConfigReport, "P", "x", false);
			cmd.setBoolean(i++, loadDefaultData, "P", "x", false);
			cmd.setNumber(i++, finalConsumerLimit, 9, 2, true);
			cmd.setNumber(i++, ticketInvoiceLimit, 9, 2, true);
			cmd.setNumber(i++, ivaNonInscript, 2, 2, true);
			cmd.setNumber(i++, copies, true);
			cmd.setBoolean(i++, printChange, "P", "x", true);
			cmd.setBoolean(i++, printLabels, "P", "x", true);
			cmd.setText(i++, ticketCutType, true);
			cmd.setBoolean(i++, printFramework, "P", "x", true);
			cmd.setBoolean(i++, reprintDocuments, "P", "x", true);
			cmd.setText(i++, balanceText, 80, true);
			cmd.setBoolean(i++, paperSound, "P", "x", true);
			cmd.setText(i++, paperSize, true);
			return cmd;
	}	
		
	public FiscalPacket cmdGeneralDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_GENERAL_DISCOUNT);
		int i = 1;
		cmd.setText(i++, description, 50, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		cmd.setBoolean(i++, substract, "m", "M", false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		return cmd;
	}
	
	public FiscalPacket cmdGetGeneralConfigurationData() {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_GENERAL_CONFIGURATION);
		return cmd;
	}

	public FiscalPacket cmdGetDateTime() {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_DATE_TIME);
		return cmd;
	}

	public FiscalPacket cmdGetEmbarkNumber(int line) {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_EMBARK_NUMBER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		return cmd;
	}

	public FiscalPacket cmdGetFantasyName(int line) {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_FANTASY_NAME);
		int i = 1;
		cmd.setNumber(i++, line, false);
		return cmd;
	}

	public FiscalPacket cmdGetHeaderTrailer(int line) {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_HEADER_TRAILER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		return cmd;
	}

	public FiscalPacket cmdGetWorkingMemory() {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_WORKING_MEMORY);
		return cmd;
	}

	public FiscalPacket cmdLastItemDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_LAST_ITEM_DISCOUNT);
		int i = 1;
		cmd.setText(i++, description, 50, false);
		cmd.setPerceptionAmount(i++, amount, false);
		cmd.setBoolean(i++, substract, "m", "M", false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		return cmd;
	}

	public FiscalPacket cmdOpenDNFH(String docType, String identification) {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_DNFH);
		int i = 1;
		cmd.setText(i++, docType, false);
		cmd.setText(i++, "T", true);
		cmd.setText(i++, identification, true);
		return cmd;
	}

	public FiscalPacket cmdOpenFiscalReceipt(String docType) {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_FISCAL_RECEIPT);
		int i = 1;
		cmd.setText(i++, docType, false);
		cmd.setText(i++, "T", true);
		return cmd;
	}

	public FiscalPacket cmdOpenNonFiscalReceipt() {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_NON_FISCAL_RECEIPT);
		return cmd;
	}

	public FiscalPacket cmdPerceptions(String description, BigDecimal amount, BigDecimal alicuotaIVA) {
		FiscalPacket cmd = createFiscalPacket(CMD_PERCEPTIONS);
		int i = 1;
		if(alicuotaIVA == null)
			cmd.setText(i++, "**.**", false);
		else
			cmd.setNumber(i++, alicuotaIVA, 2, 2, false);
		cmd.setText(i++, description, 20,false);
		cmd.setPerceptionAmount(i++, amount, false);
		return cmd;
	}

	public FiscalPacket cmdPrintAccountItem(Date date, String docNumber, String description, BigDecimal debitAmount, BigDecimal creditAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_ACCOUNT_ITEM);
		int i = 1;
		cmd.setDate(i++, date);
		cmd.setText(i++, docNumber, 20, false);
		cmd.setText(i++, description, 60, false);
		cmd.setNumber(i++, debitAmount, 9, 2, false);
		cmd.setNumber(i++, creditAmount, 9, 2, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintEmbarkItem(String description, BigDecimal quantity, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_EMBARK_ITEM);
		int i = 1;
		cmd.setText(i++, description, 108, false);
		cmd.setQuantity(i++, quantity, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintFiscalText(String text, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_FISCAL_TEXT);
		int i = 1;
		cmd.setText(i++, text, 50, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	
	}
	
	public FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes, boolean basePrice, Integer display) {
		//Most models' description max length is 50
		int maxLength = 50;
		return cmdPrintLineItem(description, quantity, price, ivaPercent, substract, internalTaxes, basePrice, display, maxLength);
	}

	//Cuspide Computacion: metodo que permite especificar la longitud mÃ¡xima de la descripcion, para los modelos que lo requieran.
	protected FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes, boolean basePrice, Integer display, int descMaxLength) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_LINE_ITEM);
		int i = 1;
		cmd.setText(i++, description, descMaxLength, false);
		cmd.setQuantity(i++, quantity, false);
		cmd.setAmount(i++, price, false);
		if(ivaPercent == null)
			cmd.setText(i++, "**.**", false);
		else
			cmd.setNumber(i++, ivaPercent, 2, 2, false);
		cmd.setBoolean(i++, substract, "m", "M", false);
		cmd.setNumber(i++, internalTaxes, 6, 8, false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, basePrice, "x", "T", false);
		return cmd;
	}

	public FiscalPacket cmdPrintNonFiscalText(String text, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_NON_FISCAL_TEXT);
		int i = 1;
		cmd.setText(i++, text, 120, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintQuotationItem(String description, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_QUOTATION_ITEM);
		int i = 1;
		cmd.setText(i++, description, 120, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdReprint() {
		FiscalPacket cmd = createFiscalPacket(CMD_REPRINT_DOCUMENT);
		return cmd;
	}

	public FiscalPacket cmdSendFirstIVA() {
		FiscalPacket cmd = createFiscalPacket(CMD_SEND_FIRST_IVA);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdNextIVATransmission() {
		FiscalPacket cmd = createFiscalPacket(CMD_NEXT_TRANSMISSION);
		return cmd;
	}
	
	public FiscalPacket cmdSetComSpeed(Long speed) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_COM_SPEED);
		int i = 1;
		cmd.setLong(i++, speed);
		return cmd;
	}

	public FiscalPacket cmdSetCustomerData(String name, String customerDocNumber, String ivaResponsibility, String docType, String location) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_CUSTOMER_DATA);
		int i = 1;
		cmd.setText(i++, name, 50, true);
		cmd.setText(i++, formatDocNumber(docType,customerDocNumber), true);
		cmd.setText(i++, ivaResponsibility, false);
		cmd.setText(i++, docType, true);
		cmd.setText(i++, location, 50, true);
		return cmd;
	}

	public FiscalPacket cmdSetDateTime(Date dateTime) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_DATE_TIME);
		cmd.setDateAndTime(1,2, dateTime);
		return cmd;
	}

	public FiscalPacket cmdSetEmbarkNumber(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_EMBARK_NUMBER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		cmd.setText(i++, text, 20, false);
		return cmd;
	}

	public FiscalPacket cmdSetFantasyName(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_FANTASY_NAME);
		int i = 1;
		cmd.setNumber(i++, line, false);
		cmd.setText(i++, text, 50, false);
		return cmd;
	}

	public FiscalPacket cmdSetHeaderTrailer(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_HEADER_TRAILER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		cmd.setText(i++, text, 120, false);
		return cmd;
	}

	public FiscalPacket cmdSTATPRN() {
		FiscalPacket cmd = createFiscalPacket(CMD_STATPRN);
		return cmd;
	}

	public FiscalPacket cmdStatusRequest() {
		FiscalPacket cmd = createFiscalPacket(CMD_STATUS_REQUEST);
		return cmd;
	}

	public FiscalPacket cmdSubtotal(boolean print, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_SUBTOTAL);
		int i = 1;
		cmd.setBoolean(i++, print, "P", "x", false);
		cmd.setString(i++, "x");
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_TOTAL_TENDER);
		int i = 1;
		cmd.setText(i++, description, 80, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		cmd.setBoolean(i++, cancel, "C", "T");
		cmd.setNumber(i++, display, true);
		return cmd;
	}
	
	/**muestra error porque en EpsonCommands falta el metodo cmdReturnRecharge **/
	@Override
	public FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display, String operation) {
		int descMaxLength = 50;
		return cmdReturnRecharge(description, amount, ivaPercent, subtract,
				internalTaxes, baseAmount, display, operation, descMaxLength);
	}
	
	protected FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display,
			String operation, int descMaxLength) {
		FiscalPacket cmd = createFiscalPacket(CMD_RETURN_RECHARGE);
		int i = 1;
		cmd.setText(i++, description, descMaxLength, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		if(ivaPercent == null){
			ivaPercent = BigDecimal.ZERO;
		}
		cmd.setNumber(i++, ivaPercent, 2, 2, false);
		cmd.setBoolean(i++, subtract, "m", "M", false);
		cmd.setNumber(i++, internalTaxes, 6, 8, false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		cmd.setText(i++, operation, false);
		return cmd;
	}
	
	protected FiscalPacket createFiscalPacket() {
		return new EpsonFiscalPacket(getEncoding(),getBaseRolloverYear(), this);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getBaseRolloverYear() {
		return baseRolloverYear;
	}

	public void setBaseRolloverYear(int baseRolloverYear) {
		this.baseRolloverYear = baseRolloverYear;
	}
	
	
	/**
	 * Ejecuta un comando fiscal en la impresora y analiza la existencia
	 * de errores en la respuesta. En caso de que se produzca algún error
	 * se propagan mediante excepciones.
	 * @param command Comando a ejecutar.
	 * @return Retorna un <code>FiscalPacket</code> que contiene la respuesta
	 * de la impresora.
	 * @throws FiscalPrinterIOException cuando se producce algún error de
	 * comunicación con el dispositivo.
	 * @throws FiscalPrinterStatusError cuando la impresora responde con un
	 * código de estado de error.
	 */
	
	protected FiscalPacket execute(FiscalPacket command) throws FiscalPrinterIOException, FiscalPrinterStatusError {
		FiscalPacket response = createFiscalPacket();

		// Se guarda el comando como el último ejecutado.
		setLastRequest(command);
		setLastResponse(null);

		try {
			// Se envía el comando a la interfaz de comunicación para
			// ser ejecutado.
			getFiscalComm().execute(command, response);
			setLastResponse(response);

		} catch (IOException e) {
			throw new FiscalPrinterIOException(e.getMessage(), command, response);
		}

		// Se chequea el status devuelto por la impresora.
		boolean statusChanged = checkStatus(response);
		
		
		// Si se produjeron cambios en el estado de la impresora se dispara
		// el evento correspondiente.
        if (statusChanged) {
            fireStatusChanged(command, response);
        }

		// Si la impresora quedó en estado de error entonces se lanza una
		// excepciÃón.
        if (getMessages().hasErrors()) {
            throw new FiscalPrinterStatusError(command, response, getMessages());
        }

		// Se informa al manejador que el comando se ejecutó satisfactoriamente.
		fireCommandExecuted(command, response);

		return response;
	}
	
	private boolean checkStatus(FiscalPacket response) throws FiscalPrinterIOException {
		int newPrinterStatus;
		int newFiscalStatus;

		try {
			// Se obtiene los estados a partir de la respuesta.
			newPrinterStatus = response.getPrinterStatus();
			newFiscalStatus = response.getFiscalStatus();
		} catch (Exception e) {
			// Se puede producir un error de formato al querer obtener los estados
			// de la respuesta. Puede suceder que solo se reciba una parte de la
			// respuesta.
			throw new FiscalPrinterIOException(MsgRepository.get("ResponseFormatError"), getLastRequest(), response);
		}

		// Se comprueba si el status fue modificado.
		boolean stsChanged = getPrinterStatus() != newPrinterStatus ||
							 getFiscalStatus() != newFiscalStatus;

		// Se asignan los estados de impresora y controlador fiscal.
		setPrinterStatus(newPrinterStatus);
		setFiscalStatus(newFiscalStatus);

		FiscalMessages msgs = new FiscalMessages();
		// Se chequea el estado del controlador fiscal.
		for(int i = 0; i < getFiscalStatusCodes().length; i++) {
			int statusCode = getFiscalStatusCodes()[i];
			if((getFiscalStatus() & statusCode) != 0) {
				FiscalMessage msg = getFiscalStatusMsgs().get(statusCode);
				msgs.add(msg);
			}
		}

		// Se chequea el estado de la impresora.
		for(int i = 0; i < getPrinterStatusCodes().length; i++) {
			int statusCode = getPrinterStatusCodes()[i];
			if((getPrinterStatus() & statusCode) != 0) {
				FiscalMessage msg = getPrinterStatusMsgs().get(statusCode);
				msgs.add(msg);
			}
			// Se chequea el estado del papel de la impresora y se
			// setea el mismo.
			if(statusCode == PST_JOURNAL_PAPER_OUT || statusCode == PST_TICKET_PAPER_OUT)
				setWithoutPaper((getPrinterStatus() & statusCode) != 0);
		}

		// Se setean los mensajes de la impresora.
		setMessages(msgs);

		return stsChanged;
	}


}
