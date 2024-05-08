/*
 * The MIT License
 *
 * Copyright 2024 Laboratorio de Bioquímica e Química de Proteínas / 
 * Instituto de Ciências Biológicas / Universidade de Brasília
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package actr.serial;

import actr.Source;
import actr.Well;
import java.util.Date;

/**
 *
 * @author gustavo_vrr
 */
public class ComandosACTR {
    private final ACTRSerial actrSerial = new ACTRSerial();
    
    private ComandosACTR()
    {
        
    }
    
    private final static ComandosACTR comandosACTR = new ComandosACTR();
    
    public static ComandosACTR getSingleTon()
    {
        return comandosACTR;
    }
    
    public boolean connect()
    {
        Date start = new Date();
        boolean response = actrSerial.connect();
        Date end = new Date();
        System.out.println("connect: " + (end.getTime()-start.getTime()));        
        return response;
    }
    
    public void disconnect()
    {
        Date start = new Date();
        actrSerial.disconnect();
        Date end = new Date();
        System.out.println("disconnect: " + (end.getTime()-start.getTime()));         
    }

    public void energizar() {
        
        Date start = new Date();
        
        byte b = (byte)(0xF0 & (Comandos.ENERGIZE.value()<<4));
        actrSerial.sendCommand(b);
        /*
        try {
            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */
        
        try {
            int millis = SettlingTime.V12_TRAIL;
            Thread.sleep(millis); 
        } catch (InterruptedException e) {
        }
        
        Date end = new Date();
        System.out.println("energizar: " + (end.getTime()-start.getTime()));
    }

    public void desenergizar() {
        Date start = new Date();
        byte b = (byte)(0xF0 & (Comandos.DEENERGIZE.value()<<4));
        actrSerial.sendCommand(b);
        
        /*
        try {

            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */
        
        
        Date end = new Date();
        System.out.println("desenergizar: " + (end.getTime()-start.getTime()));
    }

    public void holdSample() {
        Date start = new Date();
        byte b = (byte)(0xF0 & (Comandos.HOLD_SAMPLE.value()<<4));
        actrSerial.sendCommand(b);

        /*
        try {
            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */

        try {
            int millis = SettlingTime.LF398_HOLD;
            Thread.sleep(millis); 
        } catch (InterruptedException e) {
        }
        
        Date end = new Date();
        System.out.println("holdSample: " + (end.getTime()-start.getTime()));        
        
    }

    public void releaseSample() {
        Date start = new Date();
        byte b = (byte)(0xF0 & (Comandos.RELEASE_SAMPLE.value()<<4));
        actrSerial.sendCommand(b);

        /*
        try {
            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */
        
        
        try {
            int millis = SettlingTime.LF398_SAMPLE;
            Thread.sleep(millis); 
        } catch (InterruptedException e) {
        }        
        
        Date end = new Date();
        System.out.println("releaseSample: " + (end.getTime()-start.getTime()));          
    }

    public int makeADConversion() {
        Date start = new Date();
        
        byte b = (byte)(0xF0 & (Comandos.MAKE_AD_CONVERSION.value()<<4));
        actrSerial.sendCommand(b);
        
        int valor;
        try {
            byte[] valores = actrSerial.read();
            valor = ((0xFF&valores[0])) +  ((0xFF&valores[1])<<8);
            
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
            valor = 0;
        }
        Date end = new Date();
        System.out.println("makeADConversion: " + (end.getTime()-start.getTime()));         
        return valor;
    }

    public void selectWell(Well well) {
        Date start = new Date();
        byte b = (byte)(0xF0 & (Comandos.SELECT_WELL.value()<<4));
        b = (byte)(b + well.value());
        actrSerial.sendCommand(b);
        
        /*
        try {
            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */
        
        Date end = new Date();
        System.out.println("selectWell: " + (end.getTime()-start.getTime()) + " (" + well.getShortName() + ")"); 
    }


    public void selectSignal(Signal s) {
        Date start = new Date();
        byte b = (byte)(0xF0 & (Comandos.SELECT_SIGNAL.value()<<4));
        b = (byte)(b + s.value());
        actrSerial.sendCommand(b);

        /*
        try {
            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */
        
        try {
            int millis = SettlingTime.FILTER + SettlingTime.AD736;
            Thread.sleep(millis); 
        } catch (InterruptedException e) {
        }   
        
        Date end = new Date();
        System.out.println("selectSignal: " + (end.getTime()-start.getTime()) +  " (" + s + ")");         
    }

    public void selectSource(Source source) {
        Date start = new Date();
        byte b = (byte)(0xF0 & (Comandos.SELECT_SOURCE.value()<<4));
        b = (byte)(b + source.value());
        actrSerial.sendCommand(b);

        /*
        try {
            actrSerial.read();
        } catch (ACTRSerial.ReadException ex) {
            System.out.println(ex);
        }
        */
 
        
        Date end = new Date();
        System.out.println("selectSignal: " + (end.getTime()-start.getTime()) +  " (" + source + ")");             
        
    }    
    
    
    public boolean isConnected() {
        Date start = new Date();
        boolean response = actrSerial.isConnected();
        Date end = new Date();
        System.out.println("isConnected: " + (end.getTime()-start.getTime()));          
        return response;
    }
    

}
