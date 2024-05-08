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
package actr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;


public class Ensaio implements Serializable{

    private static final long serialVersionUID = 5512234731422983481L;
    
    
    private StatusEnsaio statusEnsaio = StatusEnsaio.EmEspecificacao;
    private final Agenda agenda = new Agenda(this);
    private transient EnsaioListener listener; 
    private Varredura varreduraBase;
    private final boolean[] visibleGraph = {true, true, true, true,
                                      true, true, true, true,
                                      true, true, true, true,
                                      true, true, true, true};
    private boolean normalizedCi;


    
    
    public EnsaioListener getListener() {
        return listener;
    }
    private Date dataHoraInicio = null; 

    private final ArrayList<TimerVarredura> timers = new ArrayList<>();

    

    private CIFormula ciFormula = CIFormula.Z_Zo_15;
    
    public Ensaio(EnsaioListener listener)
    {
        this.listener = listener;
        listener.ensaioCriado(this);
    }
    
    
    public CIFormula getCIFormula()
    {
        return ciFormula;
    }
    
    public void setCIFormula(CIFormula ciFormula)
    {
        this.ciFormula = ciFormula;
    }

    
   
    
    
    public void setDataHoraInicio(Date date)
    {
        this.dataHoraInicio = date;
    }
    
    public Date getDataHoraInicio()
    {
        return dataHoraInicio;
    }
    
    public StatusEnsaio getStatusEnsaio()
    {
        return statusEnsaio;
    }
    

    
    public Agenda getAgenda()
    {
        return agenda;
    }
    
    public void save(String fileName)
    {
        try {
            FileOutputStream arquivo = new FileOutputStream(fileName);
            ObjectOutputStream sequencia = new ObjectOutputStream(arquivo);
            sequencia.writeObject(this);
            sequencia.flush();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public static Ensaio load(String fileName, EnsaioListener listener)
    {
        try {        
            FileInputStream entrada = new FileInputStream(fileName);
            ObjectInputStream sequencia = new ObjectInputStream(entrada);
            Ensaio novoEnsaio = (Ensaio) sequencia.readObject();
            novoEnsaio.listener = listener;
            if(novoEnsaio.getStatusEnsaio() == StatusEnsaio.EmAndamento)
                novoEnsaio.ativarTimers();
            listener.ensaioCriado(novoEnsaio);
            return novoEnsaio;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        
        return null;
    }
    
    @Override 
    public String toString()
    {
        StringBuilder sb =  new StringBuilder();
        sb.append("Status Ensaio: " + statusEnsaio.toString() +" \n");
        if(statusEnsaio == StatusEnsaio.EmAndamento || statusEnsaio == StatusEnsaio.Finalizado)
            sb.append("DataHora início: " + getDataHoraInicio().toString() +" \n");
        sb.append("Agenda:\n");
        for(int p=0; p<getAgenda().conteProgramacoes(); p++)
        {
            Programacao programacao = getAgenda().getProgramacao(p);
            sb.append("\tProgramacao " + p + "\n");
            sb.append("\t\tPeriodo Mínimo: " + programacao.getPeriodoMinutes() + "\n");
            sb.append("\t\tQtd. Varreduras: " + programacao.getQtdVarreruras() + "\n");
            for(int v=0; v<programacao.getQtdVarreruras(); v++)
            {
                Varredura varredura = programacao.getVarredura(v);
                sb.append("\t\tVarredura " + v + "\n");
                for(Well well: Well.values())
                {
                    Leitura leitura = varredura.getLeitura(well);
                    if(leitura == null)
                        sb.append("\t\t\tWell " + well.toString()  + " -> null\n"); 
                    else if(leitura.statusLeitura == StatusLeitura.Normal)
                        sb.append("\t\t\tWell " + well.toString()  + " -> " + leitura.impedancia  + "\n");
                    else
                        sb.append("\t\t\tWell " + well.toString()  + " -> " + leitura.statusLeitura + "\n");
                }
                
            }
        }
        
        return sb.toString();
    }
    
    private void clearTimers()
    {
        for(TimerVarredura timerVarredura: timers)
        {
            timerVarredura.cancel();
        }
        timers.clear();
    }    

    public void iniciarEnsaio() {
    
        statusEnsaio = StatusEnsaio.EmAndamento;
      
        
        ExecutaVarredura executaPrimeiraVarredura = null;
        Calendar scheduleTime = Calendar.getInstance();
        
        for(int i = 0; i<agenda.conteProgramacoes();i++)
        {
            Programacao p = agenda.getProgramacao(i);
            for(int j=0; j<p.getQtdVarreruras(); j++)
            {
                Varredura varredura = p.getVarredura(j);
                
                if(i==0 && j==0)
                {
                    ExecutaVarredura executaVarredura = new ExecutaVarredura(varredura, timers);
                    Date d = scheduleTime.getTime();
                    varredura.setScheduleTime(d);
                    executaPrimeiraVarredura = executaVarredura;
                }
                else
                {
                    scheduleTime.add(Calendar.MINUTE, p.getPeriodoMinutes());
                    Date d = scheduleTime.getTime();
                    varredura.setScheduleTime(d);
                    ExecutaVarredura executaVarredura = new ExecutaVarredura(varredura, timers);
                    TimerVarredura timerVarredura = new TimerVarredura(d,executaVarredura);
                    timers.add(timerVarredura);
                    
                }

                
            }
        }
        (new Thread(executaPrimeiraVarredura)).start();
        
    }

    public void finalizarEnsaio() {
        clearTimers();
        statusEnsaio = StatusEnsaio.Finalizado;
        if(listener != null)
            listener.ensaioFinalizado(this);
        
    }

    static void removeOldTimers(ArrayList<TimerVarredura> timers) {
        
        int i=0;
        while(i<timers.size())
        {
            TimerVarredura timerVarredura = timers.get(i);
            if(timerVarredura.isOld())
            {
                timers.remove(timerVarredura);
            }
            else
                i++;
        }
        
    }

    private void ativarTimers() {
        removeOldTimers(timers);
        for(TimerVarredura timerVarredura: timers)
        {
            timerVarredura.start();
        }
        if(timers.isEmpty())
        {
            this.statusEnsaio = StatusEnsaio.Finalizado;
            this.listener.ensaioFinalizado(this);
        }
    }

    public void setVisibleGraph(Well well, boolean selected) {
        visibleGraph[well.value] = selected;
    }
    
    public boolean isVisibleGraph(Well well)
    {
        return visibleGraph[well.value];
    }

    public void setNormalizedCi(boolean value) {
        normalizedCi = value;
    }

    public boolean isNormalizedCi()
    {
        return normalizedCi;
    }

    public void updateVarreduraBase() {
        if(this.agenda.conteProgramacoes()==0)
            return;
        
        varreduraBase = agenda.getProgramacao(0).getVarredura(1);

    }
    
    
    
    public void updateVarreduraBase(Date d) {
        if(this.agenda.conteProgramacoes()==0)
            return;
        
        Varredura varredura_min = this.agenda.getProgramacao(0).getVarredura(1);
        long dif_min = Math.abs(varredura_min.getScheduleTime().getTime()-d.getTime());
        
        for(int p=0; p<agenda.conteProgramacoes(); p++)
        {
            Programacao programacao = agenda.getProgramacao(p);
            for(int v=0; v<programacao.getQtdVarreruras(); v++)
            {
                if(p!=0 || v!=0)
                {
                    Varredura varredura = programacao.getVarredura(v);
                    long dif = Math.abs(varredura.getScheduleTime().getTime()-d.getTime());
                    if(dif < dif_min)
                    {
                        dif_min=dif;
                        varredura_min=varredura;
                    }
                }
                
            }
        }
        varreduraBase = varredura_min;
        this.listener.varreduraBaseAlterada(this);    
        
    }

    public Varredura getVarreduraBase() {
        return varreduraBase;
    }

    public int countVisibleGraph() 
    {
        int r = 0;
        for(Well well: Well.values())
            if(this.isVisibleGraph(well)) r++;
        return r;
    }
            
 
}
