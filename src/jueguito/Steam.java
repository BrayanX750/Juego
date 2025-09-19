/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jueguito;

import java.io.*;

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

    public int crearJuego(String titulo, char sistema, int edadMin, double precio, String imagen) throws IOException {
        int code = nextJuegoCode();
        juegos.seek(juegos.length());
        juegos.writeInt(code);
        writeFixed(juegos, titulo, tituloSize);
        juegos.writeChar(sistema);
        juegos.writeInt(edadMin);
        juegos.writeDouble(precio);
        juegos.writeInt(0);
        writeFixed(juegos, imagen, imgSize);
        return code;
    }

    public void listarJuegos() throws IOException {
        long n = juegos.length() / tamJuego;
        for (int i = 0; i < n; i++) {
            long pos = (long)i * tamJuego;
            juegos.seek(pos);
            int c = juegos.readInt();
            String titulo = readFixed(juegos, tituloSize);
            char sis = juegos.readChar();
            int edad = juegos.readInt();
            double precio = juegos.readDouble();
            int dls = juegos.readInt();
            String img = readFixed(juegos, imgSize);
            System.out.println(c+" | "+titulo+" | "+sis+" | "+edad+" | "+precio+" | "+dls+" | "+img);
        }
    }

    public long buscarJuego(int codigo) throws IOException {
        long n = juegos.length() / tamJuego;
        for (int i = 0; i < n; i++) {
            long pos = (long)i * tamJuego;
            juegos.seek(pos);
            int c = juegos.readInt();
            if (c == codigo) return pos;
        }
        return -1;
    }

    public int crearJugador(String user, String pass, String nombre, long nacimiento, String foto, String tipo) throws IOException {
        int code = nextClienteCode();
        jugadores.seek(jugadores.length());
        jugadores.writeInt(code);
        writeFixed(jugadores, user, userSize);
        writeFixed(jugadores, pass, passSize);
        writeFixed(jugadores, nombre, nombreSize);
        jugadores.writeLong(nacimiento);
        jugadores.writeInt(0);
        writeFixed(jugadores, foto, imgSize);
        writeFixed(jugadores, tipo, tipoSize);
        return code;
    }

    public void listarJugadores() throws IOException {
        long n = jugadores.length() / tamJugador;
        for (int i = 0; i < n; i++) {
            long pos = (long)i * tamJugador;
            jugadores.seek(pos);
            int c = jugadores.readInt();
            String u = readFixed(jugadores, userSize);
            String p = readFixed(jugadores, passSize);
            String nom = readFixed(jugadores, nombreSize);
            long nac = jugadores.readLong();
            int dls = jugadores.readInt();
            String foto = readFixed(jugadores, imgSize);
            String tipo = readFixed(jugadores, tipoSize);
            System.out.println(c+" | "+u+" | "+nom+" | "+nac+" | "+dls+" | "+foto+" | "+tipo);
        }
    }

    public long buscarJugador(int codigo) throws IOException {
        long n = jugadores.length() / tamJugador;
        for (int i = 0; i < n; i++) {
            long pos = (long)i * tamJugador;
            jugadores.seek(pos);
            int c = jugadores.readInt();
            if (c == codigo) 
                return pos;
        }
        return -1;
    }

    public int registrarDescarga(int codigoJuego, int codigoJugador) throws IOException {
        long nJ = juegos.length() / tamJuego;
        for (int i=0; i<nJ; i++) {
            long pos = (long)i * tamJuego;
            juegos.seek(pos);
            int c = juegos.readInt();
            if (c == codigoJuego) {
                juegos.skipBytes((tituloSize*2)+2+4+8);
                int dls = juegos.readInt();
                juegos.seek(pos + 4 + (tituloSize*2) + 2 + 4 + 8);
                juegos.writeInt(dls+1);
            }
        }
        long nP = jugadores.length() / tamJugador;
        for (int i=0; i<nP; i++) {
            long pos = (long)i * tamJugador;
            jugadores.seek(pos);
            int c = jugadores.readInt();
            if (c == codigoJugador) {
                jugadores.skipBytes((userSize*2)+(passSize*2)+(nombreSize*2)+8);
                int dls = jugadores.readInt();
                jugadores.seek(pos + 4 + (userSize*2)+(passSize*2)+(nombreSize*2)+8);
                jugadores.writeInt(dls+1);
            }
        }
        return nextDownloadCode();
    }

    
}
