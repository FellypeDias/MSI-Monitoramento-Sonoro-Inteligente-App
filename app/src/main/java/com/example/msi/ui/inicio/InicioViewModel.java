package com.example.msi.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.msi.R;
import com.example.msi.data.network.EventoApiItem;
import com.example.msi.data.network.EventosApiRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class InicioViewModel extends ViewModel {
    private final MutableLiveData<InicioUiState> state = new MutableLiveData<>();
    private final EventosApiRepository repository = new EventosApiRepository();

    public InicioViewModel() {
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
        repository.buscarEventos(new EventosApiRepository.Callback() {
            @Override
            public void onSuccess(List<EventoApiItem> eventos) {
                state.postValue(montarEstadoDaApi(eventos));
            }

            @Override
            public void onError(Exception error) {
                // Mantem o estado neutro atual se a API falhar.
            }
        });
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
        int[] contagens = new int[6];
        for (EventoApiItem evento : eventos) {
            Date data = parseDataHora(evento.getDataHora());
            if (data == null || !isHoje(data)) {
                continue;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);
            int hora = calendar.get(Calendar.HOUR_OF_DAY);
            int indice = bucketPorHora(hora);
            contagens[indice]++;
        }

        int maior = 1;
        for (int valor : contagens) {
            if (valor > maior) {
                maior = valor;
            }
        }

        List<ChartPoint> pontos = new ArrayList<>();
        pontos.add(new ChartPoint("6h", normalizar(contagens[0], maior)));
        pontos.add(new ChartPoint("9h", normalizar(contagens[1], maior)));
        pontos.add(new ChartPoint("12h", normalizar(contagens[2], maior)));
        pontos.add(new ChartPoint("15h", normalizar(contagens[3], maior)));
        pontos.add(new ChartPoint("18h", normalizar(contagens[4], maior)));
        pontos.add(new ChartPoint("Agora", normalizar(contagens[5], maior)));
        return pontos;
    }

    private List<HistoryEvent> buildHistoryEvents(List<EventoApiItem> eventos, boolean statusOnline) {
        List<HistoryEvent> historyEvents = new ArrayList<>();
        int limite = Math.min(eventos.size(), 6);
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
        return historyEvents;
    }

    private int bucketPorHora(int hora) {
        if (hora >= 6 && hora < 9) return 0;
        if (hora >= 9 && hora < 12) return 1;
        if (hora >= 12 && hora < 15) return 2;
        if (hora >= 15 && hora < 18) return 3;
        if (hora >= 18 && hora < 21) return 4;
        return 5;
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
