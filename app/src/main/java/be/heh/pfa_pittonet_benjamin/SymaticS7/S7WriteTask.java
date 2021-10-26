package be.heh.pfa_pittonet_benjamin.SymaticS7;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class S7WriteTask {
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread writeThread;
    private AutomateS7 plcS7;
    private S7Client comS7;
    private String[] parConnexion = new String[10];
    private byte[] motCommande = new byte[512];

    public S7WriteTask() {
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
    }

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    public void Start(String a, String r, String s) {
        if (!writeThread.isAlive()) {
            parConnexion[0] = a;
            parConnexion[1] = r;
            parConnexion[2] = s;
            writeThread.start();
            isRunning.set(true);
        }
    }

    private class AutomateS7 implements Runnable {
        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(parConnexion[0],
                        Integer.valueOf(parConnexion[1]), Integer.valueOf(parConnexion[2]));
                while (isRunning.get() && (res.equals(0))) {
                    Integer writePLC = comS7.WriteArea(S7.S7AreaDB, 5, 0, 32, motCommande);
                    if (writePLC.equals(0)) {
                        Log.i("ret WRITE : ", String.valueOf(res) + "****" + String.valueOf(writePLC));
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void WriteByte(int p, int v) {
        String s = Integer.toBinaryString(v);
        ArrayList<Boolean> booleans = new ArrayList<>();

        for (char c : s.toCharArray()) {
            if (c == '1') booleans.add(true);
            else if (c == '0') booleans.add(false);
        }

        //Writre right --> left
        int i = booleans.size() - 1;
        for (Boolean b : booleans) {
            System.out.print(b + " ");
            S7.SetBitAt(motCommande, p, i, b);
            i--;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void WriteInt(int p, int v) {
        S7.SetWordAt(motCommande, p, v);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

