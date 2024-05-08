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

import java.io.Serializable;
import java.util.ArrayList;

public class Agenda implements Serializable{
    
    private static final long serialVersionUID = 3512734751822973481L;

    
    private final ArrayList<Programacao> programacoes = new ArrayList<>();
    private final Ensaio parent;
    
    public Agenda(Ensaio parent)
    {
        this.parent = parent;
    }

    public Ensaio getParent() {
        return parent;
    }
    
    public void AdicionarProgramacao(Programacao programacao)
    {
        programacoes.add(programacao);
    }
    
    public void removerProgramacao(int i)
    {
        programacoes.remove(i);
    }
    
    public void removerProgramacao(Programacao p)
    {
        programacoes.remove(p);
    }
    
    public int conteProgramacoes()
    {
        return programacoes.size();
    }
    
    public Programacao getProgramacao(int i)
    {
        return programacoes.get(i);
    }
    
    public int conteTotalVarreduras()
    {
        int r=0;
        for(int p=0; p<conteProgramacoes(); p++)
        {
            Programacao programacao = getProgramacao(p);
            r+=programacao.getQtdVarreruras();
        }
        return r;
    }
}
