package com.fullstack.rxjavatest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText phoneView = (EditText) findViewById(R.id.textView1);
        EditText usernameView = (EditText) findViewById(R.id.textView2);
        EditText emailView = (EditText) findViewById(R.id.textView3);
        final Button button = (Button) findViewById(R.id.button);

        Observable<String> emailObservable = getTextWatcherObservable(emailView);
        Observable<String> usernameObservable = getTextWatcherObservable(usernameView);
        Observable<String> phoneObservable = getTextWatcherObservable(phoneView);

        Observable.combineLatest(emailObservable, usernameObservable,
                phoneObservable, new Function3<String, String, String, Boolean>() {
                    @Override
                    public Boolean apply(String s, String s2, String s3) throws Exception {
                        //function3 will be called every time when something change
                        //the number 3 refers to the number of params (3 EditText)
                        return !TextUtils.isEmpty(s) && !TextUtils.isEmpty(s2) && !TextUtils.isEmpty(s3);
                    }
                }).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                //will be called the first time
                Log.e(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                //have the return value from the apply method
                Log.e(TAG, "onNext: " + aBoolean);
                if (aBoolean) {
                    button.setBackgroundColor(Color.GREEN);
                } else {
                    button.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                //if there is any error applied this method will be called
                Log.e(TAG, "onError: ", throwable);
            }

            @Override
            public void onComplete() {
                //for this type of combineLatest this method will not be called because it doesn't
                //depend on once returned result
                Log.e(TAG, "onComplete: ");
            }
        });

    }

    public static Observable<String> getTextWatcherObservable(@NonNull final EditText editText) {
        final PublishSubject<String> subject = PublishSubject.create();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "afterTextChanged: " + s);
                subject.onNext(s.toString());
            }
        });

        return subject;
    }
}
