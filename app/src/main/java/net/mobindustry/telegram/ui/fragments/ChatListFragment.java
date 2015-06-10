package net.mobindustry.telegram.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import net.mobindustry.telegram.R;
import net.mobindustry.telegram.ui.adapters.ChatListAdapter;

import org.drinkless.td.libcore.telegram.TdApi;

public class ChatListFragment extends ListFragment {

    boolean dualPane;
    int currentCheckPosition = 0;
    private ChatListAdapter adapter;

    private TdApi.Chats chats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new ChatListAdapter(getActivity());
        return inflater.inflate(R.layout.contact_list_fragment, null);
    }

    public void setChatsList(final TdApi.Chats chats) {
        this.chats = chats;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.i("LOG", "chatsFragment setList");
                adapter.clear();
                adapter.addAll(chats.chats);//TODO check this!!!
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.attachToListView(getListView());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Open New Message Fragment", Toast.LENGTH_SHORT).show();
            }
        });
        setListAdapter(adapter);

        View detailsFrame = getActivity().findViewById(R.id.messages);
        dualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedState != null) {
            currentCheckPosition = savedState.getInt("curChoice", 0);
        }

        if (dualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            //showMessages(currentCheckPosition);
        }
    }

    public TdApi.Chat getChat() {
        return chats.chats[currentCheckPosition];
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", currentCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        showMessages(pos);
    }

    void showMessages(int index) {
        currentCheckPosition = index;

        FragmentTransaction ft
                = getFragmentManager().beginTransaction();

        if (dualPane) {
            getListView().setItemChecked(index, true);
            MessagesFragment messagesFragment = (MessagesFragment)
                    getFragmentManager().findFragmentById(R.id.messages);
            if (messagesFragment == null || messagesFragment.getShownIndex() != index) {
                messagesFragment = MessagesFragment.newInstance(index);

                ft.replace(R.id.messages, messagesFragment);
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);

                ft.commit();
            }
        } else {
            getListView().setItemChecked(index, true);
            MessagesFragment messagesFragment = (MessagesFragment)
                    getFragmentManager().findFragmentById(R.id.messages);
            if (messagesFragment == null || messagesFragment.getShownIndex() != index) {
                messagesFragment = MessagesFragment.newInstance(index);

                ft.replace(R.id.messages, messagesFragment);
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
                ft.commit();
            }
        }
    }
}
