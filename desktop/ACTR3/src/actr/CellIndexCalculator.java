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


public class CellIndexCalculator {
    
    
    
    public static class Response
    {
        StatusLeitura statusLeitura;
        double ci;
        
        @Override
        public String toString()
        {
            switch(statusLeitura)
            {
                case Normal: return "" + ci;
                case AindaNaoRealizada: return "";
                default: return statusLeitura.toString();
            }
        }
    }
    
    
    public static Response calcCellIndex(Varredura varredura, Well well)
    {

        Response r = new Response();
        r.statusLeitura = StatusLeitura.AindaNaoRealizada;  
        r.ci = 0;


        
        Programacao programacao = varredura.getParent();
        Agenda agenda = programacao.getParent();
        Ensaio ensaio = agenda.getParent();
        Varredura primeiraVarredura = agenda.getProgramacao(0).getVarredura(0);

        Leitura l = varredura.getLeitura(well);
        
        
        if(l==null )
            return r;        
        
        
        Leitura l_0 = primeiraVarredura.getLeitura(well);
        
        
        r.statusLeitura = StatusLeitura.Normal;  
        

        if(l.statusLeitura != StatusLeitura.Normal)
            r.statusLeitura = l.statusLeitura;

        if(l_0.statusLeitura != StatusLeitura.Normal)
            r.statusLeitura = l_0.statusLeitura;
        
        
        if(r.statusLeitura != StatusLeitura.Normal)
            return r;
        
        double ci;

        if(ensaio.getCIFormula() == CIFormula.Z_Zo_17)
        {
            ci = (l.impedancia - l_0.impedancia)/17;
        }
        else if (ensaio.getCIFormula() == CIFormula.Z_Zo_15)
        {
            ci = (l.impedancia - l_0.impedancia)/15;
        }
        else if (ensaio.getCIFormula() == CIFormula.Z_Zo)
        {
            ci = (l.impedancia - l_0.impedancia);
        }
        else //if (ensaio.getCIFormula() == CIFormula.Z)
        {
            ci = l.impedancia;
        }
        
            
        r.ci = ci;
        return r;
    }
    
    
    public static Response calcNormalizedCellIndex(Varredura varredura, Well well, Varredura varreduraBase)
    {
        
        if(varreduraBase == null)
        {
            Response r = new Response();
            r.statusLeitura = StatusLeitura.AindaNaoRealizada;
            r.ci = 0;
            return r;
        }
        
        Response r_base = calcCellIndex(varreduraBase, well);
        Response r_normal = calcCellIndex(varredura, well);
        Response r = new Response();
        if(r_base.ci == 0)
        {
            r.ci = 0;
            r.statusLeitura = r_normal.statusLeitura;
            return r;
        }
        else
        {
            r.ci = r_normal.ci/r_base.ci;
            r.statusLeitura = r_normal.statusLeitura;
            return r;
        }
    }

}
