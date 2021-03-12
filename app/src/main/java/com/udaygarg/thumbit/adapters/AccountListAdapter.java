package com.udaygarg.thumbit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.udaygarg.thumbit.MainActivity;
import com.udaygarg.thumbit.R;
import com.udaygarg.thumbit.models.AccountListData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.udaygarg.thumbit.AccountActivity.imageName;

public class AccountListAdapter implements ListAdapter {
    private final ArrayList<AccountListData> listData;
    Context context;
    ListView parentListView;
    private FirebaseFunctions mFunctions;
    ProgressBar progressBarActivity;
    AlertDialog.Builder builder;

    public AccountListAdapter(Context context, ArrayList<AccountListData> listData, ListView parentListView, ProgressBar progressBarActivity) {
        this.context = context;
        this.listData = listData;
        this.parentListView = parentListView;
        this.progressBarActivity = progressBarActivity;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mFunctions = FirebaseFunctions.getInstance();
        AccountListData accountListData = listData.get(i);

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            view = layoutInflater.inflate(R.layout.accounts_item, parentListView, false);

            TextView accountTextView = view.findViewById(R.id.accountsText);
            ImageView accountImageView = view.findViewById(R.id.accountsImage);
            accountTextView.setText(accountListData.getAccountText());
            accountImageView.setImageResource(accountListData.getAccountImage());
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            LinearLayout accountLinearLayout = view.findViewById(R.id.linearLayoutAccount);


            View finalView = view;
            view.setOnClickListener(view1 -> {
                if (accountListData.getAccountText().equals("Delete my account")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);/* context problem*/
//                    View dialogView = LayoutInflater.from(finalView.getContext()).inflate(R.layout.delete_account_dialog, viewGroup, false);   //customized
//                    builder.setView(dialogView);
                    builder.setMessage("Are you sure?").setTitle("Delete Account");
                    builder
                            .setPositiveButton("Yes", (dialogInterface, i1) -> {

                                progressBarActivity.setVisibility(View.VISIBLE);
                                context.getSharedPreferences("com.udaygarg.thumbit", Context.MODE_PRIVATE).edit().clear().apply();
                                deleteAtPath("/message/" + uid);
                                deleteAtPath("/users/" + uid);
                                deleteAtPath("/messageNotification/" + uid);

                                if (!imageName.equals("")) {
                                    FirebaseStorage.getInstance().getReference().child("images").child(imageName).delete()
                                            .addOnSuccessListener(aVoid -> Log.i("image", "getView: image deleted "))
                                            .addOnFailureListener(e -> System.out.println(e.getMessage()));
                                }

                                addMessage(uid).addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Exception e = task.getException();
                                        if (e instanceof FirebaseFunctionsException) {
                                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                            FirebaseFunctionsException.Code code = ffe.getCode();
                                            Object details = ffe.getDetails();


                                        }

                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        progressBarActivity.setVisibility(View.GONE);
                                        Toast.makeText(context, "Account successfully deleted", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(intent);
                                    }


                                });

                            })
                            .setNegativeButton("Cancel", (dialogInterface, i1) -> dialogInterface.cancel());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }

            });

        }
        return view;
    }

    /**
     * Call the 'recursiveDelete' callable function with a path to initiate
     * a server-side delete.
     */
    public void deleteAtPath(String path) {
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);

        HttpsCallableReference deleteFn =
                FirebaseFunctions.getInstance().getHttpsCallable("recursiveDelete");
        deleteFn.call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        System.out.println("delete success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("delete failed");
                    }
                });
    }


    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);


        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    String result = (String) task.getResult().getData();

                    return result;
                });
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return listData.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
