"""
conftest.py — Base fixtures for Airline UI Automation (Python/Pytest).
All WebDriver lifecycle management is handled here.
"""

import os
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

BASE_URL = os.getenv("BASE_URL")  # Set via environment variable — no hardcoded default
HEADLESS = os.getenv("HEADLESS", "false").lower() == "true"
BROWSER = os.getenv("BROWSER", "chrome")


@pytest.fixture(scope="function")
def driver():
    """
    Function-scoped WebDriver fixture.
    Each test gets a fresh browser session.
    """
    options = Options()
    options.add_argument("--start-maximized")
    options.add_argument("--disable-notifications")
    options.add_argument("--disable-popup-blocking")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")

    if HEADLESS:
        options.add_argument("--headless=new")

    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    driver.implicitly_wait(0)  # Disable implicit waits — use explicit only
    driver.set_page_load_timeout(30)

    driver.get(BASE_URL)

    yield driver

    driver.quit()


@pytest.fixture(scope="session")
def base_url():
    """Session-scoped base URL fixture."""
    return BASE_URL


@pytest.fixture(scope="session")
def credentials():
    """Session-scoped credentials from environment variables."""
    return {
        "email": os.getenv("TEST_USER_EMAIL"),
        "password": os.getenv("TEST_USER_PASSWORD"),
    }
