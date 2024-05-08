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

import com.fazecast.jSerialComm.SerialPort;
import java.util.Date;

public class ACTRSerial {
    
    private final int EXPECTED_BYTES=2;
    
    public class ReadException extends Exception
    {
        public ReadException(String message)
        {
            super(message);
        }
    }
    
    //lembre-se de usar esse comando linux se necessario:
    //sudo usermod -a -G dialout username

    private SerialPort serialPort;
    
    public ACTRSerial()
    {

            
    }
    
    public boolean isConnected()
    {
        if(serialPort==null)
            return false;
        else
            return serialPort.isOpen();
    }
    
    public boolean connect()
    {
        SerialPort[] sp = SerialPort.getCommPorts();
        for(int i=0; i<sp.length; i++)
        {
            if(sp[i].getDescriptivePortName().equals("ANALISE DE CELULA EM TEMPO REAL"))
            {
                serialPort = sp[i];
            }
        }        
        
        if(serialPort!=null)
        {
            serialPort.setBaudRate(115200);
            serialPort.openPort();
            return true;
        }
        
        return false;
    }
    
    public void disconnect()
    {
        if(serialPort != null)
            serialPort.closePort();
    }
    
    private void write(byte[] b)
    {
        serialPort.writeBytes(b,b.length);
    }
    
    public void sendCommand(byte b)
    {
        
        write(new byte[]{b});
        
        
    }
    

    //gerar excessao em timeout ou quando sobrar bytes alem dos 128
    public byte[] read() throws ReadException
    {
        Date dateInicio = new Date();
        while(serialPort.bytesAvailable()==0)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                
            }
            if((new Date()).getTime() - dateInicio.getTime() > 20000)
                throw new ReadException("Timeout");
        }
        //esperamos expected bytes
        byte[] b = new byte[EXPECTED_BYTES];
        serialPort.readBytes(b, EXPECTED_BYTES);
        
        if(serialPort.bytesAvailable()>0)
        {
            throw new ReadException("Dados sobrantes");
        }
        
        return b;
    }
    
    
}
