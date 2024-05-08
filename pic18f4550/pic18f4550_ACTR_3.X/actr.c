#include <xc.h>
#include <pic18f4550.h>
#include <string.h>
#include "actr.h"


void initACTR(void)
{
    OSCCONbits.IDLEN = 1;   // Habilitar modo IDLE    
    // Configura��o do Porta D como sa�da
    TRISA = 0x00;
    TRISB = 0x00;
    TRISD = 0x00;
    
    
    
    LATDbits.LATD7 = 0; //hold inicia ligado (sen�o corrente vaza pelo pino 8 do LF398 - comportamento assim s� ocorre com LF398 desenergizado)

    LATAbits.LATA5 = 1; 
    LATAbits.LATA4 = 0; //source ser� o Well (not Cal)   
    
    TRISAbits.TRISA0 = 1; //define o pino RA0 e AN0 como entrada
    TRISAbits.TRISA2 = 1; //define o pino RA2 e AN2 como entrada
    TRISAbits.TRISA3 = 1; //define o pino RA3 e AN3 como entrada


    ADCON0bits.CHS = 0; //informa que a entrada � em RA0/AN0 (pin. 2)
    ADCON0bits.ADON = 1; //m�dulo habilitado o tempo todo
    ADCON1bits.VCFG0 = 1; //refer�ncia positiva em RA3 (n�o VCC)
    ADCON1bits.VCFG1 = 1; //refer�ncia negativa em  RA2 (n�o VSS)
    ADCON1bits.PCFG = 0; //informa que o sinal na porta � anal�gico
    ADCON2bits.ADFM = 1; //resultado justificado � direita
    ADCON2bits.ACQT = 0; //informa que o tempo de aquisi��o � manual
    ADCON2bits.ADCS = 0b110; //informa que o clock a ser usado na convers�o � 
                             //o mais lento dispon�vel FOSC/64 (depois experimentar 101 - FOSC/16)
        
}

void energize()
{
    // Colocar o pino RD4 em estado alto para habilitar 12 volts
    LATDbits.LATD4 = 1;  
}

void deenergize()
{
    // Colocar o pino RD4 em estado baixo para habilitar 12 volts
    LATDbits.LATD4 = 0;  
}

void selectWell(unsigned char well)
{
    LATDbits.LATD0 = well & 0x0001;    
    LATDbits.LATD1 = (well >> 1) & 0x0001;    
    LATDbits.LATD2 = (well >> 2) & 0x0001;    
    LATDbits.LATD3 = (well >> 3) & 0x0001;     
}


void selectSignal(unsigned char signal)
{
    LATDbits.LATD5 = signal & 0x0001;  
    LATDbits.LATD6 = (signal >> 1) & 0x0001;  
}


void holdSample()
{
    LATDbits.LATD7 = 0;
}    


void releaseSample()
{
    LATDbits.LATD7 = 1;
}


void makeADConversion(unsigned short* value)
{
    ADCON0bits.GO_NOT_DONE = 1; //convers�o � iniciada
    
 
    
    while(ADCON0bits.GO_NOT_DONE == 1){}
    *value = (ADRESH << 8) + ADRESL;
    
}

void selectSource(unsigned char source)
{
    switch(source)
    {
        case SOURCE_WELL:
            LATAbits.LATA5 = 1;
            LATAbits.LATA4 = 0;    
            break;
        case SOURCE_CAL:
            LATAbits.LATA4 = 1;    
            LATAbits.LATA5 = 0;
            break;
        default:
            LATAbits.LATA4 = 1;    
            LATAbits.LATA5 = 1;
            
            
            
            
    }
}