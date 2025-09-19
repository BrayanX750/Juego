/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jueguito;

import java.io.*;
import java.util.*;

public class Steam {

    private final RandomAccessFile codes;
    private final RandomAccessFile juegos;
    private final RandomAccessFile jugadores;

   
    private final int tituloSize = 40;
    private final int imgSize = 120;
    private final int userSize = 20;
    private final int passSize = 20;
    private final int nombreSize = 30;
    private final int tipoSize = 12;

   
    private final int tamJuego   = 4 + (tituloSize*2) + 2 + 4 + 8 + 4 + (imgSize*2);
    private final int tamJugador = 4 + (userSize*2) + (passSize*2) + (nombreSize*2) + 8 + 4 + (imgSize*2) + (tipoSize*2);

    public Steam() throws IOException {
        File dir = new File("steam");
        if (!dir.exists()) dir.mkdirs();

        File fCodes = new File(dir, "codes.stm");
        File fGames = new File(dir, "games.stm");
        File fPlayers = new File(dir, "player.stm");

        codes = new RandomAccessFile(fCodes, "rw");
        juegos = new RandomAccessFile(fGames, "rw");
        jugadores = new RandomAccessFile(fPlayers, "rw");

        if (codes.length() == 0) {
            codes.seek(0);
            codes.writeInt(1); 
            codes.writeInt(1); 
            codes.writeInt(1); 
        }
    }

    
    private void writeFixed(RandomAccessFile raf, String s, int len) throws IOException {
        if (s == null) s = "";
        if (s.length() > len) s = s.substring(0, len);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.append(' ');
        raf.writeChars(sb.toString());
    }

    private String readFixed(RandomAccessFile raf, int len) throws IOException {
        char[] c = new char[len];
        for (int i = 0; i < len; i++) c[i] = raf.readChar();
        return new String(c).trim();
    }

    private int nextJuegoCode() throws IOException {
        codes.seek(0);
        int cj = codes.readInt();
        codes.seek(0);
        codes.writeInt(cj + 1);
        return cj;
    }

    private int nextClienteCode() throws IOException {
        codes.seek(4);
        int cc = codes.readInt();
        codes.seek(4);
        codes.writeInt(cc + 1);
        return cc;
    }

    private int nextDownloadCode() throws IOException {
        codes.seek(8);
        int cd = codes.readInt();
        codes.seek(8);
        codes.writeInt(cd + 1);
        return cd;
    }

    public static class Juego {
        public int code;
        public String titulo;
        public char sistema;        
        public int edadMin;
        public double precio;
        public int downloads;
        public String imagenPath;

        @Override
        public String toString() {
            return code + " | " + titulo + " | " + sistema + " | " + edadMin + " | $" + precio + " | dls:" + downloads;
        }
    }

    public static class Jugador {
        public int code;
        public String username;
        public String password;
        public String nombre;
        public long nacimiento;     
        public int downloads;
        public String fotoPath;
        public String tipo;         

        @Override
        public String toString() {
            return code + " | " + username + " | " + nombre + " | tipo:" + tipo + " | dls:" + downloads;
        }
    }

 
    public int crearJuego(String titulo, char sistema, int edadMin, double precio, String imagenPath) throws IOException {
        int code = nextJuegoCode();
        juegos.seek(juegos.length());
        juegos.writeInt(code);
        writeFixed(juegos, titulo, tituloSize);
        juegos.writeChar(sistema);
        juegos.writeInt(edadMin);
        juegos.writeDouble(precio);
        juegos.writeInt(0); 
        writeFixed(juegos, imagenPath, imgSize);
        return code;
    }

    public Juego leerJuegoPorPos(long pos) throws IOException {
        juegos.seek(pos);
        Juego j = new Juego();
        j.code = juegos.readInt();
        j.titulo = readFixed(juegos, tituloSize);
        j.sistema = juegos.readChar();
        j.edadMin = juegos.readInt();
        j.precio = juegos.readDouble();
        j.downloads = juegos.readInt();
        j.imagenPath = readFixed(juegos, imgSize);
        return j;
    }

    public long buscarPosJuegoPorCodigo(int code) throws IOException {
        long n = juegos.length() / tamJuego;
        for (int i = 0; i < n; i++) {
            long pos = (long)i * tamJuego;
            juegos.seek(pos);
            int c = juegos.readInt();
            if (c == code) return pos;
        }
        return -1;
    }

    public Juego buscarJuego(int code) throws IOException {
        long p = buscarPosJuegoPorCodigo(code);
        if (p < 0) return null;
        return leerJuegoPorPos(p);
    }

    public List<Juego> listarJuegos() throws IOException {
        List<Juego> lista = new ArrayList<>();
        long n = juegos.length() / tamJuego;
        for (int i = 0; i < n; i++) {
            lista.add(leerJuegoPorPos((long)i * tamJuego));
        }
        return lista;
    }

    
    public int crearJugador(String username, String password, String nombre, long nacimiento, String fotoPath, String tipo) throws IOException {
        int code = nextClienteCode();
        jugadores.seek(jugadores.length());
        jugadores.writeInt(code);
        writeFixed(jugadores, username, userSize);
        writeFixed(jugadores, password, passSize);
        writeFixed(jugadores, nombre, nombreSize);
        jugadores.writeLong(nacimiento);
        jugadores.writeInt(0);
        writeFixed(jugadores, fotoPath, imgSize);
        writeFixed(jugadores, tipo, tipoSize);
        return code;
    }

    public Jugador leerJugadorPorPos(long pos) throws IOException {
        jugadores.seek(pos);
        Jugador p = new Jugador();
        p.code = jugadores.readInt();
        p.username = readFixed(jugadores, userSize);
        p.password = readFixed(jugadores, passSize);
        p.nombre = readFixed(jugadores, nombreSize);
        p.nacimiento = jugadores.readLong();
        p.downloads = jugadores.readInt();
        p.fotoPath = readFixed(jugadores, imgSize);
        p.tipo = readFixed(jugadores, tipoSize);
        return p;
    }

    public long buscarPosJugadorPorCodigo(int code) throws IOException {
        long n = jugadores.length() / tamJugador;
        for (int i = 0; i < n; i++) {
            long pos = (long)i * tamJugador;
            jugadores.seek(pos);
            int c = jugadores.readInt();
            if (c == code) return pos;
        }
        return -1;
    }

    public Jugador buscarJugador(int code) throws IOException {
        long p = buscarPosJugadorPorCodigo(code);
        if (p < 0) return null;
        return leerJugadorPorPos(p);
    }

    public List<Jugador> listarJugadores() throws IOException {
        List<Jugador> lista = new ArrayList<>();
        long n = jugadores.length() / tamJugador;
        for (int i = 0; i < n; i++) {
            lista.add(leerJugadorPorPos((long)i * tamJugador));
        }
        return lista;
    }

   
    public int registrarDescarga(int codigoJuego, int codigoJugador) throws IOException {
        long posJ = buscarPosJuegoPorCodigo(codigoJuego);
        long posP = buscarPosJugadorPorCodigo(codigoJugador);
        if (posJ < 0 || posP < 0) return -1;

        
        juegos.seek(posJ + 4 + (tituloSize*2) + 2 + 4 + 8); 
        int dlsJuego = juegos.readInt();
        juegos.seek(posJ + 4 + (tituloSize*2) + 2 + 4 + 8);
        juegos.writeInt(dlsJuego + 1);

        
        jugadores.seek(posP + 4 + (userSize*2) + (passSize*2) + (nombreSize*2) + 8); 
        int dlsPlayer = jugadores.readInt();
        jugadores.seek(posP + 4 + (userSize*2) + (passSize*2) + (nombreSize*2) + 8);
        jugadores.writeInt(dlsPlayer + 1);

        int codDescarga = nextDownloadCode();
        return codDescarga;
    }

    

}

