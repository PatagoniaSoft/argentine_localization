package ar.edu.ifes.print.fiscal.epson;

import java.math.BigDecimal;

import ar.com.ergio.print.fiscal.FiscalPacket;
import ar.com.ergio.print.fiscal.comm.FiscalComm;



public  class EpsonPrinterU220AF extends EpsonFiscalPrinter {

	public EpsonPrinterU220AF {
		super(); //llamas al constructor de EpsonFiscalPrinter
	}
	/**
	 * @param fiscalComm
	 */
	public EpsonPrinterU220AF(FiscalComm fiscalComm) {
		super(fiscalComm);
	}









}
