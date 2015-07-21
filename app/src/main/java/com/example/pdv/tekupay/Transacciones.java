package com.example.pdv.tekupay;

public class Transacciones {
	 
	   private String pan;
	   private String monto;
	   private String fecha;
	   private String hora;
	   private int transactionStatus;
	   private String tipoTarjeta;
	   private int cierre;
	   private String referencia;
	 
	   public Transacciones(String pan, String monto, String fecha, String hora, int transactionStatus, String tipoTarjeta, int cierre, String referencia) {
	      this.pan = pan;
	      this.monto = monto;
	      this.fecha = fecha;
	      this.hora = hora;
	      this.transactionStatus = transactionStatus;
	      this.tipoTarjeta = tipoTarjeta;
	      this.cierre = cierre;
	      this.referencia = referencia;
	   }
	 
	   public void setPan(String pan) { this.pan = pan; }
	 
	   public String getPan() { return pan; }
	 
	   public void setMonto(String monto) { this.monto = monto; }
	 
	   public String getMonto() { return monto; }
	 
	   public void setFecha(String fecha) { this.fecha = fecha; }
	 
	   public String getFecha() { return fecha; }
	 
	   public void setHora(String hora) { this.hora = hora; }
	 
	   public String getHora() { return hora; }
	 
	   public void setTransactionStatus(int transactionStatus) { this.transactionStatus = transactionStatus; }
	 
	   public int getTransactionStatus() { return transactionStatus; }
	 
	   public void setTipoTarjeta(String tipoTarjeta) { this.tipoTarjeta = tipoTarjeta; }
	 
	   public String getTipoTarjeta() { return tipoTarjeta; }
	 
	   public void setCierre(int cierre) { this.cierre = cierre; }
	 
	   public int getCierre() { return cierre; }
	 
	   public void setReferencia(String referencia) { this.referencia = referencia; }
	 
	   public String getReferencia() { return referencia; }
}
