package utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by leonardocordeiro on 30/07/16.
 */

public class DialogUtils {

    private Context context;

    public DialogUtils(Context context) {
        this.context = context;
    }

    public void showConnectionError(final OnRetryListener successListener) {
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                     /* TODO: extract strings to strings.xml */
                    .setMessage("Não foi possível contactar o servidor, verifique seu acesso a Internet")
                    .setTitle("Sem conexão")
                    .setPositiveButton("Tentar novamente", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            successListener.onRetry();
                        }
                    }).create().show();

    }

    public interface OnRetryListener {
        void onRetry();
    }

}
