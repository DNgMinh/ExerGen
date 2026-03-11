package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionHistoryUseCaseTest {

    @Mock
    private ISessionHistoryRepository mockRepository;

    private SessionHistoryUseCase useCase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new SessionHistoryUseCase(mockRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullRepository() {
        new SessionHistoryUseCase(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveCompletedSessionRejectsNullRecord() {
        useCase.saveCompletedSession(null);
    }

    @Test
    public void saveCompletedSessionPersistsRecord() {
        SessionRecord record = createRecord("s-1", 1700000000000L);
        useCase.saveCompletedSession(record);
        verify(mockRepository).saveSession(record);
    }

    @Test
    public void getSessionHistoryReturnsNewestFirst() {
        SessionRecord older = createRecord("older", 1000L);
        SessionRecord middle = createRecord("middle", 2000L);
        SessionRecord newest = createRecord("newest", 3000L);

        // Repository returns unsorted list
        when(mockRepository.getAllSessions()).thenReturn(Arrays.asList(older, newest, middle));

        List<SessionRecord> results = useCase.getSessionHistory();

        assertEquals(3, results.size());
        assertEquals("newest", results.get(0).getId()); // Logic: UseCase should sort them
        assertEquals("middle", results.get(1).getId());
        assertEquals("older", results.get(2).getId());
    }

    @Test
    public void getSessionHistoryReturnsEmptyListWhenRepositoryReturnsNull() {
        when(mockRepository.getAllSessions()).thenReturn(null);
        List<SessionRecord> results = useCase.getSessionHistory();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void getSessionByIdReturnsMatchingSession() {
        SessionRecord record = createRecord("lookup-id", 1000L);
        when(mockRepository.getSessionById("lookup-id")).thenReturn(record);

        SessionRecord result = useCase.getSessionById("lookup-id");

        assertNotNull(result);
        assertEquals("lookup-id", result.getId());
        verify(mockRepository).getSessionById("lookup-id");
    }

    @Test
    public void getSessionByIdReturnsNullForBlankId() {
        assertNull(useCase.getSessionById("   "));
        verifyNoInteractions(mockRepository);
    }

    private static SessionRecord createRecord(String id, long completedAtEpochMs) {
        return new SessionRecord(id, "w1", "Workout A", completedAtEpochMs, 600, 5, 3, 3);
    }
}
