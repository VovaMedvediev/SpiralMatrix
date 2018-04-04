package com.example.vovka.spiralmatrix;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static int[] sArrayOfSixes = {0, 1, 19, 271, 3439, 40951, 468559};
    private int mMatrixSize;
    private int mMinMatrixSize = 1;
    private int mMaxMatrixSize = 1000;
    private List<String> mList;
    private EditText mEditTextMatrixSize;
    private SimpleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextMatrixSize = (EditText) findViewById(R.id.edit_text_matrix_size);
        mEditTextMatrixSize.requestFocus();
    }

    /**Gets user's input, validates it and builds matrix.
     * @param view clicked button
     */
    public void showMatrix(View view) {
        TextInputLayout inputWrapper = (TextInputLayout) findViewById(R.id.text_input_layout);
        String matrixSize = mEditTextMatrixSize.getText().toString().trim();

        if (!validateInput(matrixSize)) {
            inputWrapper.setError(getResources().getString(R.string.error_message));
        } else {
            inputWrapper.setErrorEnabled(false);

            final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
            hideKeyboard();
            relativeLayout.animate().alpha(0).setDuration(1500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    relativeLayout.setVisibility(View.GONE);
                }
            });

            mMatrixSize = Integer.valueOf(matrixSize);
            initList();
            initRecycler();
            new ProgressTask().execute();
        }
    }

    /**
     * Setting up the RecyclerView, animations.
     */
    private void initRecycler() {
        AnimationItem mAnimationItem = new AnimationItem("Fall down",
                R.anim.layout_animation_fall_down);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        FixedGridLayoutManager manager = new FixedGridLayoutManager();
        manager.setTotalColumnCount(mMatrixSize);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new SimpleAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_small);
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
        runLayoutAnimation(mRecyclerView, mAnimationItem);
    }

    /**
     * Initialize ArrayList with empty values.
     */
    private void initList() {
        mList = new ArrayList<>(mMatrixSize * mMatrixSize);
        for (int i = 0; i < mMatrixSize * mMatrixSize; i++) {
            mList.add("");
        }
    }

    /** Runs the layout animation.
     */
    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, item.getResourceId());
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * This method need for automatically hide of keyboard after go button click.
     */
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * @param input User's number for square matrix.
     * @return True if sizeOfMatrix has possible value.
     */
    private boolean validateInput(String input) {
        int sizeOfMatrix = 0;
        if(!input.equals("")) {
            sizeOfMatrix = Integer.valueOf(input);
        }
        return sizeOfMatrix >= mMinMatrixSize && sizeOfMatrix <= mMaxMatrixSize;
    }

    /**
     * @param number = sizeOfMatrix * sizeOfMatrix.
     * @param excludedNumber Number which we want to exclude(6).
     * @return count of number contained excludedNumber.
     */
    private static int countExcludedNumber(int number, int excludedNumber) {
        int currentStep = 0;
        int prevStep = -1;

        while (currentStep != prevStep) {
            prevStep = currentStep;
            int tempNumb = number + prevStep;
            currentStep = getNumberOfSixs(tempNumb, excludedNumber);
        }

        return currentStep;
    }

    /**
     * @param number = sizeOfMatrix * sizeOfMatrix.
     * @param excludedNumber Number which we want to exclude(6).
     * @return count of number contained excludedNumber at one pass.
     */
    private static int getNumberOfSixs(Integer number, int excludedNumber) {
        int result = 0;
        int temp = number;
        for (int i = number.toString().length() - 1; i > 0; i--) {
            double pow = Math.pow(10, i);
            double divider = Math.floor(temp / pow);
            if (divider < excludedNumber) {
                result += (int) ((sArrayOfSixes[i - 1] * 9 + Math.pow(10, i - 1)) * divider);
            } else if (divider == excludedNumber) {
                result += (int) ((sArrayOfSixes[i - 1] * 9 + Math.pow(10, i - 1)) * divider)
                        + temp % (int) pow;
                return result;
            } else {
                result += (int) ((sArrayOfSixes[i - 1] * 9 + Math.pow(10, i - 1)) * (divider - 1))
                        + (int) pow;
            }

            temp %= (int) pow;
            if (temp == 0) break;
        }

        if (number % 10 > excludedNumber) result +=1;

        return result;
    }

    /**
     * This class contains algorithm for spiral filling of the matrix. Also it notifies adapter for
     * every new element in the mList.
     */
    class ProgressTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            CharSequence excludedNumber = "6";
            Integer value = mMatrixSize*mMatrixSize +
                    countExcludedNumber(mMatrixSize*mMatrixSize,
                            Integer.valueOf((String) excludedNumber));
            Integer minCol = 0;
            Integer maxCol = mMatrixSize-1;
            Integer minRow = 0;
            Integer maxRow = mMatrixSize-1;
            while(value>0){
                for(int i = minRow; i <= maxRow; i++){
                    while(String.valueOf(value).contains(excludedNumber)){
                        value--;
                    }
                    mList.set(i * mMatrixSize + minCol, value.toString());
                    try {
                        addDelay();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Integer finalMinCol = minCol;
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(finalI * mMatrixSize + finalMinCol);
                        }
                    });
                    value--;
                }
                for(int i = minCol+1; i <= maxCol; i++){
                    while(String.valueOf(value).contains(excludedNumber)){
                        value--;
                    }
                    mList.set(maxRow * mMatrixSize + i, value.toString());
                    try {
                        addDelay();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Integer finalMaxRow = maxRow;
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(finalMaxRow * mMatrixSize + finalI);
                        }
                    });
                    value--;
                }
                for(int i = maxRow-1; i >= minRow; i--){
                    while(String.valueOf(value).contains(excludedNumber)){
                        value--;
                    }
                    mList.set(i * mMatrixSize  + maxCol, value.toString());
                    try {
                        addDelay();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Integer finalMaxCol = maxCol;
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(finalI * mMatrixSize + finalMaxCol);
                        }
                    });
                    value--;
                }
                for(int i = maxCol-1; i >= minCol+1; i--){
                    while(String.valueOf(value).contains(excludedNumber)){
                        value--;
                    }
                    mList.set(minRow * mMatrixSize + i, value.toString());
                    try {
                        addDelay();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Integer finalMinRow = minRow;
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(finalMinRow * mMatrixSize + finalI);
                        }
                    });
                    value--;
                }
                minCol++;
                minRow++;
                maxCol--;
                maxRow--;
            }
            return null;
        }

        /**Add delay into algorithm for the effect of one by one filling.
         * @throws InterruptedException
         */
        private void addDelay() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(250);
        }

        /**Calls when matrix filling is finished.
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Matrix filling finished!", Toast.LENGTH_SHORT).show();
        }
    }
}
