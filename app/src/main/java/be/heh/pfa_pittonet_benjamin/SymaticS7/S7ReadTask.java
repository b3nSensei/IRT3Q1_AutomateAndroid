package be.heh.pfa_pittonet_benjamin.SymaticS7;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class S7ReadTask {
    private static final int MESSAGE_PRE_EXECUTE = 1;
    private static final int MESSAGE_PROGRESS_UPDATE = 2;
    private static final int MESSAGE_POST_EXECUTE = 3;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private ArrayList<TextView> tvs;

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];

    public S7ReadTask(View v, ArrayList tvs) {
        vi_main_ui = v;
        this.tvs = tvs;
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        readThread = new Thread(plcS7);
    }

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void Start(String a, String r, String s) {
        if (!readThread.isAlive()) {
            param[0] = a;
            param[1] = r;
            param[2] = s;
            readThread.start();
            isRunning.set(true);
        }
    }

    private void downloadOnPreExecute(int t) {
        //text.setText("PLC : " + String.valueOf(t));

    }

    @SuppressLint("SetTextI18n")
    private void downloadOnProgressUpdate(int progress) {
        if (tvs.get(0).getText().equals("Tablettes de comprimés")){
            //System.out.println(S7.GetBitAt(datasPLC, 8, 3) + " " +S7.GetBitAt(datasPLC, 8, 2) + " " +S7.GetBitAt(datasPLC, 8, 1) + " " +S7.GetBitAt(datasPLC, 8, 0));
            //Bottles
            tvs.get(1).setText("Bouteilles remplies: " + String.valueOf(S7.GetWordAt(datasPLC, 16)));
            //Pills
            if (S7.GetBitAt(datasPLC, 4, 3)) tvs.get(2).setText("Comprimés demandés:  5");
            else if (S7.GetBitAt(datasPLC, 4, 4)) tvs.get(2).setText("Comprimés demandés:  10");
            else if (S7.GetBitAt(datasPLC, 4, 5)) tvs.get(2).setText("Comprimés demandés:  15");
            else tvs.get(2).setText("Comprimés demandés:  0");
            //Operation
            if (S7.GetBitAt(datasPLC, 0, 0)) tvs.get(3).setText("En service: Oui");
            else tvs.get(3).setText("En service: Non");
            //Motor
            if (S7.GetBitAt(datasPLC, 4, 1)) tvs.get(4).setText("Moteur de bande: En marche");
            else tvs.get(4).setText("Moteur de bande: à l'arret");

        } else if (tvs.get(0).getText().equals("Niveau de liquide")){
            if (S7.GetBitAt(datasPLC,0, 5)) { tvs.get(1).setText("Mode: Auto");}
            else {tvs.get(1).setText("Mode: Manuel");}

            tvs.get(2).setText("Valeur manuel: " + String.valueOf(S7.GetWordAt(datasPLC,20)));
            tvs.get(3).setText("Valeur de sortie: " + String.valueOf(S7.GetWordAt(datasPLC,22)));
            tvs.get(4).setText("Setpoint: " + String.valueOf(S7.GetWordAt(datasPLC,18)));
            tvs.get(5).setText("Niveau: " + String.valueOf(S7.GetWordAt(datasPLC,16)) + "l");
        }
    }

    private void downloadOnPostExecute() {
        //text.setText("PLC : /!\\");
    }

    @SuppressLint("HandlerLeak")
    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_PROGRESS_UPDATE:
                    downloadOnProgressUpdate(msg.arg1);
                    break;
                case MESSAGE_POST_EXECUTE:
                    downloadOnPostExecute();
                    break;
                default:
                    break;
            }
        }
    };

    private class AutomateS7 implements Runnable {
        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(param[0], Integer.valueOf(param[1]), Integer.valueOf(param[2]));
                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                System.out.println("4");
                int numCPU = -1;
                System.out.println("5");
                if (res.equals(0) && result.equals(0)) {
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5, 8));
                } else numCPU = 0000;
                sendPreExecuteMessage(numCPU);
                while (isRunning.get()) {
                    if (res.equals(0)) {
                        int retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 0, 34, datasPLC);
                        int data = 0;
                        if (retInfo == 0) {
                            data = S7.GetWordAt(datasPLC, 0);
                            sendProgressMessage(data);
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendPostExecuteMessage();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        private void sendPostExecuteMessage() {
            Message postExecuteMsg = new Message();
            postExecuteMsg.what = MESSAGE_POST_EXECUTE;
            monHandler.sendMessage(postExecuteMsg);
        }

        private void sendPreExecuteMessage(int v) {
            Message preExecuteMsg = new Message();
            preExecuteMsg.what = MESSAGE_PRE_EXECUTE;
            preExecuteMsg.arg1 = v;
            monHandler.sendMessage(preExecuteMsg);
        }

        private void sendProgressMessage(int i) {
            Message progressMsg = new Message();
            progressMsg.what = MESSAGE_PROGRESS_UPDATE;
            progressMsg.arg1 = i;
            monHandler.sendMessage(progressMsg);
        }
    }
}
