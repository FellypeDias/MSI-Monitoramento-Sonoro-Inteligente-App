package com.example.msi.ui.inicio;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.msi.R;
import com.example.msi.data.network.EventoApiItem;
import com.example.msi.data.network.EventosApiRepository;
import com.example.msi.notifications.AlertNotificationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class InicioViewModel extends AndroidViewModel {
    private static final long REFRESH_INTERVAL_MS = 30000L;

    private final MutableLiveData<InicioUiState> state = new MutableLiveData<>();
    private final EventosApiRepository repository = new EventosApiRepository();
    private final AlertNotificationHelper notificationHelper;
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            carregarDaApi();
            refreshHandler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };
    private volatile boolean isLoading;

    public InicioViewModel(@NonNull Application application) {
        super(application);
        notificationHelper = new AlertNotificationHelper(application);
        state.setValue(new InicioUiState(
                false,
                "Offline",
                "Sem dados",
                "0",
                "0",
                "0h",
                new ArrayList<>(),
                new ArrayList<>()
        ));
        carregarDaApi();
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
    }

    public LiveData<InicioUiState> getState() {
        return state;
    }

    public void applyApiData(
            boolean statusOnline,
            String statusLabel,
            String statusLastUpdated,
            String eventosHoje,
            String alertasAtivos,
            String tempoOnline,
            List<ChartPoint> chartPoints,
            List<HistoryEvent> historyEvents
    ) {
        state.setValue(new InicioUiState(
                statusOnline,
                statusLabel,
                statusLastUpdated,
                eventosHoje,
                alertasAtivos,
                tempoOnline,
                chartPoints,
                historyEvents
        ));
    }

    private void carregarDaApi() {
        if (isLoading) {
            return;
        }
        isLoading = true;

        repository.buscarEventos(new EventosApiRepository.Callback() {
            @Override
            public void onSuccess(List<EventoApiItem> eventos) {
                isLoading = false;
                notificationHelper.notifyNewAlerts(eventos);
                state.postValue(montarEstadoDaApi(eventos));
            }

            @Override
            public void onError(Exception error) {
                isLoading = false;
                // Mantem o estado neutro atual se a API falhar.
            }
        });
    }

    @Override
    protected void onCleared() {
        refreshHandler.removeCallbacks(refreshRunnable);
        super.onCleared();
    }

    private InicioUiState montarEstadoDaApi(List<EventoApiItem> eventos) {
        List<EventoApiItem> eventosOrdenados = new ArrayList<>();
        if (eventos != null) {
            eventosOrdenados.addAll(eventos);
        }

        Collections.sort(eventosOrdenados, (a, b) -> {
            Date dataA = parseDataHora(a.getDataHora());
            Date dataB = parseDataHora(b.getDataHora());
            if (dataA == null && dataB == null) return 0;
            if (dataA == null) return 1;
            if (dataB == null) return -1;
            return dataB.compareTo(dataA);
        });

        Date agora = new Date();
        Date ultimoEvento = null;
        Date primeiroEventoHoje = null;
        int eventosHoje = 0;
        int alertasAtivos = 0;

        for (EventoApiItem evento : eventosOrdenados) {
            Date data = parseDataHora(evento.getDataHora());
            if (data != null) {
                if (isHoje(data)) {
                    eventosHoje++;
                    if (primeiroEventoHoje == null || data.before(primeiroEventoHoje)) {
                        primeiroEventoHoje = data;
                    }
                }
                if (ultimoEvento == null || data.after(ultimoEvento)) {
                    ultimoEvento = data;
                }
            }

            if ("alarme".equalsIgnoreCase(evento.getTipoEvento())) {
                alertasAtivos++;
            }
        }

        boolean statusOnline = ultimoEvento != null && minutesBetween(ultimoEvento, agora) <= 10;
        String statusLabel = statusOnline ? "Online" : "Offline";
        String statusLastUpdated = formatLastUpdated(ultimoEvento, agora);
        String tempoOnline = formatTempoOnline(primeiroEventoHoje, agora);
        List<ChartPoint> chartPoints = buildChartPoints(eventosOrdenados);
        List<HistoryEvent> historyEvents = buildHistoryEvents(eventosOrdenados, statusOnline);

        return new InicioUiState(
                statusOnline,
                statusLabel,
                statusLastUpdated,
                String.valueOf(eventosHoje),
                String.valueOf(alertasAtivos),
                tempoOnline,
                chartPoints,
                historyEvents
        );
    }

    private List<ChartPoint> buildChartPoints(List<EventoApiItem> eventos) {
        int[] contagens = new int[7];
        Date inicioSemana = getInicioSemanaAtual();
        Date fimSemana = getFimSemanaAtual(inicioSemana);

        for (EventoApiItem evento : eventos) {
            Date data = parseDataHora(evento.getDataHora());
            if (data == null || data.before(inicioSemana) || !data.before(fimSemana)) {
                continue;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);
            int indice = indicePorDiaSemana(calendar.get(Calendar.DAY_OF_WEEK));
            contagens[indice]++;
        }

        int maior = 1;
        for (int valor : contagens) {
            if (valor > maior) {
                maior = valor;
            }
        }

        List<ChartPoint> pontos = new ArrayList<>();
        String[] labels = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        for (int i = 0; i < labels.length; i++) {
            pontos.add(new ChartPoint(labels[i], normalizar(contagens[i], maior), contagens[i]));
        }
        return pontos;
    }

    private List<HistoryEvent> buildHistoryEvents(List<EventoApiItem> eventos, boolean statusOnline) {
        List<HistoryEvent> historyEvents = new ArrayList<>();
        
        // Dados da API limitados a 5
        int limite = Math.min(eventos.size(), 5);
        for (int i = 0; i < limite; i++) {
            EventoApiItem evento = eventos.get(i);
            Date data = parseDataHora(evento.getDataHora());
            String titulo = "alarme".equalsIgnoreCase(evento.getTipoEvento())
                    ? "Alarme acionado"
                    : "Campainha acionada";
            String hora = data == null ? "--:--" : new SimpleDateFormat("HH:mm", Locale.getDefault()).format(data);
            int dotEsquerdo = "alarme".equalsIgnoreCase(evento.getTipoEvento())
                    ? R.drawable.pontoverde
                    : R.drawable.pontolaranja;
            int dotDireito = statusOnline ? R.drawable.pontoverde : R.drawable.pontolaranja;

            historyEvents.add(new HistoryEvent(titulo, hora, dotEsquerdo, dotDireito));
        }

        // Fallback: Se a API estiver vazia ou tiver poucos dados, adicionamos mocks para teste visual
        if (historyEvents.size() < 3) {
            historyEvents.add(new HistoryEvent("Movimento detectado - Corredor", "10:34", R.drawable.pontolaranja, R.drawable.pontoverde));
            historyEvents.add(new HistoryEvent("Porta frontal aberta", "09:15", R.drawable.pontolaranja, R.drawable.pontoverde));
            historyEvents.add(new HistoryEvent("Sistema armado", "08:00", R.drawable.pontolaranja, R.drawable.pontoverde));
        }

        // Garante o máximo de 5
        if (historyEvents.size() > 5) {
            return historyEvents.subList(0, 5);
        }
        return historyEvents;
    }

    private Date getInicioSemanaAtual() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getFimSemanaAtual(Date inicioSemana) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inicioSemana);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        return calendar.getTime();
    }

    private int indicePorDiaSemana(int diaSemana) {
        switch (diaSemana) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            default:
                return 6;
        }
    }

    private int normalizar(int valor, int maior) {
        if (maior <= 0) return 12;
        int min = 12;
        int max = 92;
        return min + ((max - min) * valor / maior);
    }

    private String formatLastUpdated(Date ultimoEvento, Date agora) {
        if (ultimoEvento == null) {
            return "Sem atualizacao";
        }

        long minutos = minutesBetween(ultimoEvento, agora);
        if (minutos < 60) {
            return "Atualizado ha " + Math.max(1, minutos) + " min";
        }

        long horas = Math.max(1, minutos / 60);
        return "Atualizado ha " + horas + "h";
    }

    private String formatTempoOnline(Date primeiroEventoHoje, Date agora) {
        if (primeiroEventoHoje == null) {
            return "0h";
        }

        long minutos = minutesBetween(primeiroEventoHoje, agora);
        long horas = Math.max(1, minutos / 60);
        return horas + "h";
    }

    private long minutesBetween(Date inicio, Date fim) {
        long diff = Math.max(0L, fim.getTime() - inicio.getTime());
        return diff / (60L * 1000L);
    }

    private boolean isHoje(Date data) {
        Calendar agora = Calendar.getInstance();
        Calendar comparacao = Calendar.getInstance();
        comparacao.setTime(data);
        return agora.get(Calendar.YEAR) == comparacao.get(Calendar.YEAR)
                && agora.get(Calendar.DAY_OF_YEAR) == comparacao.get(Calendar.DAY_OF_YEAR);
    }

    private Date parseDataHora(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        List<String> formatos = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mm:ssX",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss"
        );

        for (String formato : formatos) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.US);
                sdf.setLenient(true);
                return sdf.parse(valor);
            } catch (ParseException ignored) {
            }
        }

        try {
            return new Date(Long.parseLong(valor));
        } catch (Exception ignored) {
            return null;
        }
    }

}
