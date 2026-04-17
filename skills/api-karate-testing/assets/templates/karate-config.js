// karate-config.js
// Environment-aware configuration for {AIRLINE_NAME} API Tests
// Adapt: replace {airline-id}, {AIRLINE_NAME}, endpoint paths, and auth type.

function fn() {
  // --- Environment selection ---
  const env = karate.env || 'dev';
  karate.log('Running against environment:', env);

  // --- Helpers for config resolution ---
  const get = (key, fallback = '') =>  karate.properties[key] || java.lang.System.getenv(key) || fallback;

  // --- Base config ---
  const config = {
    env,

    // --- Credentials ---
    clientId: get('API_CLIENT_ID'),
    clientSecret: get('API_CLIENT_SECRET'),
    apiKey: get('API_KEY'),
    testCardToken: get('TEST_CARD_TOKEN'),

    // --- Test data ---
    testOriginIata: get('TEST_ORIGIN_IATA', 'MAD'),
    testDestIata: get('TEST_DEST_IATA', 'BCN'),

    // --- Timeouts ---
    connectTimeout: 10000,
    readTimeout: 30000
  };

  // --- Environment configs ---
  const envConfig = {
    dev: {
      baseUrl: get('API_BASE_URL_DEV', 'https://api-dev.airline.com'),
      authUrl: get('AUTH_URL_DEV', 'https://auth-dev.airline.com')
    },
    qa: {
      baseUrl: get('API_BASE_URL_QA', 'https://api-qa.airline.com'),
      authUrl: get('AUTH_URL_QA', 'https://auth-qa.airline.com')
    },
    prod: {
      baseUrl: get('API_BASE_URL_PROD', 'https://api.airline.com'),
      authUrl: get('AUTH_URL_PROD', 'https://auth.airline.com')
    }
  };

  // --- Validate environment ---
  if (!envConfig[env])
    throw new Error(`Environment '${env}' not supported`);


  // --- Merge configs ---
  const finalConfig = { ...config, ...envConfig[env] };

  // --- Global HTTP config ---
  karate.configure('connectTimeout', finalConfig.connectTimeout);
  karate.configure('readTimeout', finalConfig.readTimeout);

  // --- Retry (optional but recommended) ---
  karate.configure('retry', { count: 3, interval: 2000 });

  // --- Correlation ID (one per run) ---
  const correlationId = `${java.util.UUID.randomUUID()}`;

  // --- Headers ---
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-Correlation-ID': correlationId
  };

  // --- Auth strategies ---
  if (finalConfig.apiKey)
    headers['x-api-key'] = finalConfig.apiKey;


  if (finalConfig.clientId && finalConfig.clientSecret) {
    const authResult = karate.callSingle(
      'classpath:helpers/auth-helper.feature',
      finalConfig
    );
    headers['Authorization'] = `Bearer ${authResult.token}`;
  }

  karate.configure('headers', headers);

  return finalConfig;
}